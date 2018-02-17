# Managrams

Managrams is a simple json api built in Clojure for storing and retrieving anagrams of English-language words. 

### Why do I care?

Managrams takes advantage of the [Fundamental theorem of arithmetic](https://en.wikipedia.org/wiki/Fundamental_theorem_of_arithmetic) to create unique numeric keys for each anagram. Mapping the letters to prime numbers and then using the product of those numbers as the key pointing to the collection of associated words allows to use a more efficient [int-map](https://github.com/clojure/data.int-map) data structure to store our words. The word collections for each themselves are Sets, so we get avoid having to create programmatic solutions to prevent duplicate data.

I know what you're thinking: won't long words create an integer overflow for your key values? You're right! In testing, we see that words around 12 letters long run into the upper limit for integer values we can use as keys, so Managrams optimizes for words of 10 letters or less by storing them in an int-map and longer words are stored in a more common hash-map. In the hash-map, the keys are just the alphabetically sorted letters for each anagram.

Because the majority of words come in at 10 letters or less, we get the improved performance of int-map for the majority of cases, but we don't sacrifice the ability to store longer words.

## Dependencies

[Leiningen](https://github.com/technomancy/leiningen/blob/stable/README.md)

Follow the install instructions sited in the link.

## Usage

### Run the application locally

`lein ring server-headless <optional port - default=3000>`

### Endpoints

- `POST /words.json`: Takes a JSON array of English-language words and adds them to the corpus. Returns the words submitted for the post.
- `GET /anagrams/:word.json`:
  - Returns a JSON array of English-language words that are anagrams of the word passed in the URL.
  - This endpoint supports an optional `limit` integer query param to indicate the number of words returned. 
- `DELETE /words/:word.json`: Deletes a single word from the data store.
- `DELETE /words.json`: Deletes all contents of the data store.
- `DELETE /anagrams/:word.json`: Deletes a word and its anagrams from the data store.

### Running Tests

`lein test` will run all the tests living in the root `test` directory.

### What's Lacking

- The endpoints themselves are too dumb. Adding error handling and better responses would be my first addition.
- It would be pretty easy to remove white space from words to allow us to match phrases as well as words.
- The words are stored in memory and so do not persist if the server restarts. While loading a complete word list is somewhat trivial, it would be good to add a persistant data store to back up additions that might not be included in the word list.

### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

## License

Copyright © Nothing To See Here LLC
