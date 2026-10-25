import xmlrpc.client, sys, sqlite3


name = "http://"+sys.argv[1]+":8889"

server = xmlrpc.client.ServerProxy(name)

# for stress testing multiple requests

"""
while server is running, run,

python3 Client2.py localhost &
python3 Client2.py localhost &
python3 Client2.py localhost &
wait

in terminal, to test for errors that come with concurrent requests
"""
for i in range(50):
    server.bookstore.Search("college life")

print("finished stress testing 1")

# stress testing database being accessed one request at a time

# each client will attempt to purchase the same book

# only 1 purchase should go through, other requests should 
# report a failed purchase, and database final stock should be 0

result = server.bookstore.Buy(53477)
print("Purchase result:", result)
print("finished stress testing 2", result)