import xmlrpc.client, sys, sqlite3
import xmlrpc.client, sys, time

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

# connect to the server (the server name comes from the command line)
server = xmlrpc.client.ServerProxy("http://" + sys.argv[1] + ":8889")

# show formatted output
print('Search results for topic "college life":')
for book in server.bookstore.Search("college life"):
    print("  ", book["id"], "-", book["title"])

info = server.bookstore.Lookup(53477)
print("Lookup of item 53477:")
print("   Title:", info["title"])
print("   Price: $%.2f" % info["price"])
print("   In stock:", info["in_stock"])

if server.bookstore.Buy(53477):
    print("bought book 53477")

# time 500 search requests, one after another 
start = time.time()                      # note the time before
for i in range(500):
    server.bookstore.Search("college life")
end = time.time()                        # note the time after
print("Average search time (ms):", (end - start) / 500 * 1000)

# time 500 buy requests, one after another 
start = time.time()
for i in range(500):
    server.bookstore.Buy(53477)
end = time.time()
print("Average buy time (ms):", (end - start) / 500 * 1000)
