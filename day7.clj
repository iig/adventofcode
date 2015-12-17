;; simplified dataset. find z. only single letter symbols are relveant to finding z
123 -> x
456 -> y
x AND y -> d
x OR y -> e
x LSHIFT 2 -> ff
y RSHIFT 2 -> g
NOT x -> h
NOT y -> ii
ff AND ii -> nn
d AND g -> j
e OR h -> k
j OR k -> ll
j AND k -> m
1 AND m -> z

;; some helpers
(defn znot
  [x]
  (+ 65536 (bit-not x)))

(defn zrshift
  [n x]
  (bit-shift-right x n))
  
(defn zlshift
  [n x]
  (bit-shift-left x n))
  
(def zand bit-and)
(def zor bit-or)

;; 
