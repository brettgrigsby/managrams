(ns managrams.processor
  (require [managrams.primes :as p]
           [managrams.letters :as l]))

(defn matches-for-length [word]
  (if (> (count word) 10)
    (l/match-words word)
    (p/prime-match-words word)))

(defn anagrams-for [word limit]
  (let [matching-words (matches-for-length word)]
    (if (= limit -1)
      matching-words
      (take limit (into [] matching-words)))))

(def length-limit 10)

(defn filter-words [f words]
  (filter #(f (count %) length-limit) words))

(defn process-new-words [words]
  (let [big-uns (filter-words > words)
        small-fries (filter-words <= words)]
    (do 
      (l/update-all-words big-uns)
      (p/update-all-prime-words small-fries)
      words)))

(defn delete-all-words []
  (do
    (l/reset-big-words)
    (p/reset-small-words)))

(defn delete-word [word]
  (if (> (count word) 10)
    (l/delete-big-word word)
    (p/delete-small-word word)))

(defn delete-word-and-anagrams [word]
  (let [all-words (conj (anagrams-for word -1) word)]
    (doseq [ w all-words]
      (delete-word w))))
