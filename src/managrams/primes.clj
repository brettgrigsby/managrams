(ns managrams.primes
  (:require [clojure.data.int-map :as im]
            [clojure.core.reducers :as r]))

(def dictionary-words (.getFile (clojure.java.io/resource "resources/dictionary.txt")))

(def dictionary-list
  (clojure.string/split dictionary-words #"\r\n"))

(def prime-map {\a 2 \b 3 \c 5 \d 7 \e 11 \f 13 \g 17 \h 19
  \i 23 \j 29 \k 31 \l 37 \m 41 \n 43 \o 47 \p 53 \q 59 \r 61
  \s 67 \t 71 \u 73 \v 79 \w 83 \x 89 \y 97 \z 101})

(defn prime-product [word]
  (reduce *' (map prime-map word)))

(defn add-words 
  ([] (im/int-map))
  ([intmap word] 
    (let [key (prime-product word)
          current-words (or (intmap key) [])]
      (assoc intmap key (conj current-words word)))))

(defn words->int-map [words]
  (r/fold im/merge add-words words))

(def all-prime-words (atom (words->int-map (filter #(<= (count %) 10) dictionary-list))))

(defn update-all-prime-words [words]
  (swap! all-prime-words conj (words->int-map words)))

(defn prime-match-words [word]
  (remove #(= word %) (@all-prime-words (prime-product word))))

(defn reset-small-words []
  (reset! all-prime-words (im/int-map)))

(defn delete-small-word [word]
  (let [other-matches (prime-match-words word)]
    (swap! all-prime-words assoc (prime-product word) other-matches)))
