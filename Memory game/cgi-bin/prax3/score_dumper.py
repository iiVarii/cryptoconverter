#!/usr/bin/python
# coding=utf-8
import ast
import cgi, cgitb
import json

cgitb.enable()

JSON_FILENAME = "score_data.json"
print "Content-type: text/html"
print

print "<html><head><title>score_dumper.py</title></head><body><h1>Score saved</h1><p>Data was: "

formdata = cgi.FieldStorage()
if formdata.has_key("data"):

    with open(JSON_FILENAME, 'r') as fp:
        json_data = json.load(fp)

    previous_data = ast.literal_eval(json.dumps(json_data))

    new_data = formdata["data"].value

    new_data = ast.literal_eval(new_data)

    for el in previous_data:
        if el not in new_data:
            new_data.append(el)

    print new_data

    with open(JSON_FILENAME, 'w') as fp:
        json.dump(new_data, fp)
else:
    print "none"

print ".</p></body>"
