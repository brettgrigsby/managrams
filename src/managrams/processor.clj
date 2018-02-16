(ns managrams.processor
  (require  [clojure.string :as str]
            [managrams.primes :as p]
            [managrams.letters :as l]))

(defn strip-ext [word]
  (first (str/split word #"\.")))

(defn matches-for-length [word]
  (if (> (count word) 10)
    (l/match-words word)
    (p/prime-match-words word)))

(defn anagrams-for [gross-word limit]
  (let [word (strip-ext gross-word)
        matching-words (matches-for-length word)]
    (if (= limit -1)
      matching-words
      (take limit (into [] matching-words)))))

(defn process-new-words [words]
  (let [big-uns (filter #(> (count %) 10) words)
        small-fries (filter #(<= (count %) 10) words)]
    (do 
      (l/update-all-words big-uns)
      (p/update-all-prime-words small-fries))))

(defn delete-all-words []
  (do
    (l/reset-big-words)
    (p/reset-small-words)))

(defn delete-word [gross-word]
  (let [word (strip-ext gross-word)]
    (if (> (count word) 10)
      (l/delete-big-word word)
      (p/delete-small-word word))))