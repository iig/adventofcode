(ns adventofcode.day10)

;; this reduction is a bit lispy but basically i'm going through the string and using a
;; stack to keep track of how many consecutive repetitions are there; since it is a stack
;; it will come out backwards, so we need to reverse it, and then faltten and stringify
;; coerces characters and numbers into their stringy form
(defn looknsay
  [s]
  (->> (seq s)
       (reduce (fn [all x] (if (= x (last (first all)))                       ;is the next char the same as previous
                             (cons [(inc (first (first all))) x] (rest all))  ;if so increment the count
                             (cons [1 x] all)))                               ;otherwise put a new char/count pair on the stack
               '())
       reverse
       flatten
       (apply str)))

;; do this for 41
(->> idx
     (iterate looknsay)
     (take 41)
     last
     seq
     count)


;; do the same for 51. I wonder if having 16gb of ram is cheating:)
(->> idx
     (iterate looknsay)
     (take 51)
     last
     seq
     count)
