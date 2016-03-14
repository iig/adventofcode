(ns adventofcode.day12)

;;again, why write a text parser when you have regexes?
(->> input
     (re-seq #"-?\d+")
     (map read-string)
     (reduce +))

;; part 2: to be continued
