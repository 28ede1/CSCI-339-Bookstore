import xmlrpc.client, sys

name = "http://"+sys.argv[1]+":8889"

server = xmlrpc.client.ServerProxy(name)

print("Python Implementation, TESTING, Search()")
answer1 = server.bookstore.Search("college life")

print("Python Implementation, TESTING, Lookup()")
answer2 = server.bookstore.Lookup(53477)

print("Python Implementation, TESTING, Buy()")
answer3 = server.bookstore.Buy(53477)

print ("Search Results:", answer1)
print ("Lookup Results:", answer2)
print ("Buy Results:", answer3)