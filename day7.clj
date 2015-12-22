;; simplified dataset. find a. only single letter symbols are relveant to finding a
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
1 AND m -> b
b -> a

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

;; so here's a little parser

(defn parser
  [instructions]
  (let [re-find-groups #(rest (re-find %1 %2))
        one-arg-op (partial re-find-groups #"(\w+) ->")
        shift-op (partial re-find-groups #"(\w+)[A-Z ]+(\d+)")
        two-arg-op (partial re-find-groups #"(\w+)\s\w+\s(\w+)")
        parse-shift (fn [f s] 
                      (let [[var num] (shift-op s)]
                        {:op (partial f (Integer/parseInt num)) :args [var]}))]
    (reduce (fn [x s] (assoc x (last (re-find #"-> (\w+)" s))
                               (cond
                                 (re-find #"1\sAND" s) {:op (partial zand 1) :args (one-arg-op s)}
                                 (re-find #"AND" s) {:op zand :args (two-arg-op s)}
                                 (re-find #"OR" s) {:op zor :args (two-arg-op s)}
                                 (re-find #"LSHIFT" s) (parse-shift zlshift s)
                                 (re-find #"RSHIFT" s) (parse-shift zrshift s)
                                 (re-find #"NOT" s) {:op znot :args (one-arg-op s)}
                                 (re-find #"\d" s) {:op (partial identity (-> (one-arg-op s) first Integer/parseInt))}
                                 :else {:op identity :args (one-arg-op s)}))) 
            {} (clojure.string/split-lines instructions))))

;; so now we have a map of the defs of all the variables. 
