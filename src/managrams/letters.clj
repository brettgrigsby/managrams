(ns managrams.letters
  (require [clojure.string :as str]))

(def all-words (atom {}))

(defn word->key [word]
  (str/join (sort (str/lower-case word))))

(defn match-words [word]
  (remove #(= word %) (@all-words (word->key word))))

(defn word-map-reducer
  ([] {})
  ([coll word]
    (let [key (word->key word)]
      (assoc coll key (conj (or (coll key) #{}) (str/lower-case word))))))

(defn words->hmap [words]
  (reduce word-map-reducer {} words))

(defn update-all-words [words]
  (swap! all-words conj (words->hmap words)))

(defn reset-big-words []
  (reset! all-words {}))

(defn delete-big-word [the-word]
  (let [word (str/lower-case the-word)
        other-matches (match-words word)]
    (swap! all-words assoc (word->key word) other-matches)))