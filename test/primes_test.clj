(ns primes-test
  (:use clojure.test)
  (:require [managrams.primes :as p]))

(def word "pizza")

(def words ["pasta" "bread" "salad"])

(def anagram-words ["read" "dear" "dare"])

(def mix-words ["read" "dear" "booty"])

(deftest find-product-for-letter
  (is (= 2 (p/prime-product "a")))
  (is (= 3 (p/prime-product "b")))
  (is (= 7 (p/prime-product "d"))))

(deftest find-product-for-word
  (is (= 30 (p/prime-product "abc")))
  (is (= 30 (p/prime-product "cab")))
  (is (= 45640149 (p/prime-product "booty"))))

(deftest create-int-map-for-words
  (is (= 
    {28182 #{"bread"} 69412 #{"salad"} 1008484 #{"pasta"}}
    (p/words->int-map words)))
  (is (=
    {9394 #{"dare" "dear" "read"}}
    (p/words->int-map anagram-words)))
  (is (=
    {9394 #{"dear" "read"}, 45640149 #{"booty"}}
    (p/words->int-map mix-words))))


(deftest find-matching-anagrams
  (p/update-all-prime-words words)
  (is (= ["salad"] (p/prime-match-words "dalas")))
  
  (p/update-all-prime-words anagram-words)
  (is (= ["dare" "dear"] (p/prime-match-words "read")))
  (is (= ["salad"] (p/prime-match-words "dalas")))
  
  (p/update-all-prime-words mix-words)
  (is (= [] (p/prime-match-words "booty"))))

(deftest do-not-add-repeat-words
  (p/update-all-prime-words anagram-words)
  (is (= ["dare" "dear"] (p/prime-match-words "read")))
  
  (p/update-all-prime-words anagram-words)
  (is (not= ["dare" "dear" "dare" "dear"] (p/prime-match-words "read")))
  (is (= ["dare" "dear"] (p/prime-match-words "read"))))

(deftest remove-words
  (p/update-all-prime-words mix-words)
  (is (= ["dare" "dear"] (p/prime-match-words "read")))
  
  (p/delete-small-word "dare")
  (is (= ["dear"] (p/prime-match-words "read"))))

(deftest remove-all-words
  (p/update-all-prime-words anagram-words)
  (is (= ["dare" "dear"] (p/prime-match-words "read")))
  
  (p/reset-small-words)
  (is (= [] (p/prime-match-words "read"))))