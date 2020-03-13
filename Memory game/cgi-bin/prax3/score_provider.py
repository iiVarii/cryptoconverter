#!/usr/bin/python
# coding=utf-8
import ast
import cgi, cgitb
import json

JSON_FILENAME = "score_data.json"

cgitb.enable()

with open(JSON_FILENAME, 'r') as fp:
    json_data = json.load(fp)
print "Content-type: text/html\n\n"

data_list = ast.literal_eval(json.dumps(json_data))

sorted_items = sorted(data_list, key=lambda x: (-x[1], x))

print json.dumps(sorted_items[0:min(len(sorted_items), 10)])
