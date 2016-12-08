(ns adventofcode.x2016.day2
  (:require [clojure.string :as str]
            [clojure.set :as set]))
;1 2 3
;4 5 6
;7 8 9

;; make me a mapping of keypad to operation results :

(defn decode [decoder input]
  (rest (reductions
          #(reduce (fn [[pos] next] (vector (get-in decoder [pos next]))) %1 %2)
          [5] (str/split-lines input))))

(let [decoder (into {}
                    (map #(vector % {\R (if (= 0 (mod % 3)) % (inc %))
                                     \L (if (= 0 (mod (+ 2 %) 3)) % (dec %))
                                     \D (if (> % 6) % (+ 3 %))
                                     \U (if (< % 4) % (- % 3))}))
                    (range 1 10))
      input "ULL\nRRDDD\nLURDL\nUUUUD"]
  (decode decoder input))

;; the solver stays the same so all we need is a new decoder
;     1
;   2 3 4
; 5 6 7 8 9
;   A B C
;     D

(let [edges (set/union (disj (into #{} (range 1 6)) 3)
                       (disj (into #{} (range 9 14)) 11))
      exclude-then (fn [exclude transform %]
                     (if (contains? (apply (partial disj edges) exclude)
                                     %)
                        % (transform %)))
      decoder (into {}
                    (map #(vector % {\R (exclude-then [2 5 10] inc %)
                                     \L (exclude-then [4 9 12] dec %)
                                     \D (exclude-then [1 2 4]
                                                      (fn [n] (if (contains? #{1 11} n)
                                                                (+ n 2)
                                                                (+ n 4))) %)
                                     \U (exclude-then [10 12 13]
                                                      (fn [n] (if (contains? #{3 13} n)
                                                                (- n 2)
                                                                (- n 4))) %)}))
                    (range 1 14))
      input "ULL\nRRDDD\nLURDL\nUUUUD"]
  (decode decoder input))
