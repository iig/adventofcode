;since it's an "add all things" type of problem, reduce seemed inviting

(reduce + (map (fn [line] (->> (clojure.string/split line #"x")
                               (map #(Integer/parseInt %))
                               ((fn [[x y z]] [(* x y) (* x z) (* y z)])) ;the combinatorics library wants to set your collection first so bruteforce it
                               sort                                       ;smallest
                               (map * [3 2 2])
                               (apply +)))
               (clojure.string/split-lines dims)))
 
;the next setp funny enough is actually easier thanks to the power of destructuring

(reduce + (map (fn [line] (->> (clojure.string/split line #"x")
                               (map #(Integer/parseInt %))
                               sort
                               ((fn [[x y z :as d]] (+ (* 2 (+ x y)) (apply * d))))))
               (clojure.string/split-lines dims)))
