# Lugle

## Running the Program

java -jar Lugle.jar [path to document folder]

The program is run using the command above. If no path is specified the current folder will be used.
You can create a new jar file using gradle shadowJar.


## Function

After running the program the given directory and subdirectories (or the given file) will be indexed.
When indexing is done the user is promted to enter a query. When the query is entered the index is searched and the 10 best matching results are displayed.
The program is able to index and search files of the type "text/plain" (plaintext), "text/html" (html) and "application/pdf" (pdf).

To exit the program press ctrl-d or ctrl-c

