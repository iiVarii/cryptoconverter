#!/usr/bin/python
# coding=utf-8
import ast
import cgi, cgitb
import json

cgitb.enable()

JSON_FILENAME = "score_data.json"
print "Content-type: text/html"
print

print '<!DOCTYPE html>'
print '<html lang="en">'

print '<head>'
print '<meta charset="UTF-8">'
print '    <link rel="stylesheet" href="/~mmerim/prax3/css/styles.css">'
print '</head>'
print '<body>'

subheading_text = "<h2>Top 10</h2>"

with open(JSON_FILENAME, 'r') as fp:
    json_data = json.load(fp)

previous_data = ast.literal_eval(json.dumps(json_data))

formdata = cgi.FieldStorage()

if formdata.has_key("search"):

    keyword = formdata["search"].value
    subheading_text = "<h2>Keyword: " + keyword + "</h2>"

    return_data = [el for el in previous_data if keyword.lower() in el[0].lower()]

else:
    return_data = sorted(previous_data, key=lambda x: (-x[1], x))
    return_data = return_data[0:min(len(return_data), 10)]

print "<h1>Scores</h1>"
print "<br>"
print subheading_text
print '<table align="center" class="customTable">'
print '<tbody></tbody>'
print '<tr class="tablePadding">'

print '<th class="tablePadding">Name</th>'
print '<th class="tablePadding">Score</th>'
print '<th class="tablePadding">Time</th>'
print '</tr>'

for el in return_data:
    print '<tr class="tablePadding">'
    print '<td class="tablePadding">' + str(el[0]) + '</td>'
    print '<td class="tablePadding">' + str(el[1]) + '</td>'
    print '<td class="tablePadding">' + str(el[2]) + '</td>'
    print '</tr>'

print '</table>'

print '<br>'

print '''<div style="text-align: center">
    <input id="searchField" class="searchField" type="text" placeholder="...">
    <button id="searchButton" onclick="search()">Search</button>
</div>'''

print '<br>'

print '''<div style="text-align: center">
        <button onclick="location.href='/~mmerim/prax3/index.html'" type="button">Back to game</button>
      </div>'''

print '<script src="/~mmerim/prax3/scripts.js"></script>'

print "</body>"
