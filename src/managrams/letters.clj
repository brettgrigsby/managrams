(ns managrams.letters
  (require [clojure.string :as str]))

(def all-words (atom {}))

(defn word->key [word]
  (str/join (sort word)))

(defn match-words [word]
  (remove #(= word %) (@all-words (word->key word))))

(defn word-map-reducer
  ([] {})
  ([coll word]
    (let [key (word->key word)]
      (assoc coll key (conj (or (coll key) #{}) word)))))

(defn words->hmap [words]
  (reduce word-map-reducer {} words))

(defn update-all-words [words]
  (swap! all-words conj (words->hmap words)))

(defn reset-big-words []
  (reset! all-words {}))

(defn delete-big-word [word]
  (let [other-matches (match-words word)]
    (swap! all-words assoc (word->key word) other-matches)))