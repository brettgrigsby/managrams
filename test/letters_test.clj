(ns letters-test
  (:use clojure.test)
  (:require [managrams.letters :as l]))

(def word "pizza")

(def words ["pasta" "bread" "salad"])

(def anagram-words ["read" "dear" "dare"])

(def mix-words ["read" "dear" "booty"])

(deftest find-key-for-letter
  (is (= "a" (l/word->key "a")))
  (is (= "d" (l/word->key "d"))))

(deftest find-key-for-word
  (is (= "abc" (l/word->key "abc")))
  (is (= "abc" (l/word->key "cab")))
  (is (= "aaabnn" (l/word->key "banana"))))

(deftest create-int-map-for-words
  (is (= 
    {"abder" #{"bread"} "aadls" #{"salad"} "aapst" #{"pasta"}}
    (l/words->hmap words)))
  (is (=
    {"ader" #{"dare" "dear" "read"}}
    (l/words->hmap anagram-words)))
  (is (=
    {"ader" #{"dear" "read"}, "booty" #{"booty"}}
    (l/words->hmap mix-words))))


(deftest find-matching-anagrams
  (l/update-all-words words)
  (is (= ["salad"] (l/match-words "dalas")))
  
  (l/update-all-words anagram-words)
  (is (= ["dare" "dear"] (l/match-words "read")))
  (is (= ["salad"] (l/match-words "dalas")))
  
  (l/update-all-words mix-words)
  (is (= [] (l/match-words "booty"))))

(deftest do-not-add-repeat-words
  (l/update-all-words anagram-words)
  (is (= ["dare" "dear"] (l/match-words "read")))
  
  (l/update-all-words anagram-words)
  (is (not= ["dare" "dear" "dare" "dear"] (l/match-words "read")))
  (is (= ["dare" "dear"] (l/match-words "read"))))

(deftest remove-words
  (l/update-all-words anagram-words)
  (is (= ["dare" "dear"] (l/match-words "read")))
  
  (l/delete-big-word "dare")
  (is (= ["dear"] (l/match-words "read"))))

(deftest remove-all-words
  (l/update-all-words anagram-words)
  (is (= ["dare" "dear"] (l/match-words "read")))
  
  (l/reset-big-words)
  (is (= [] (l/match-words "read"))))