;so far today started really easy. since the provided route is basically a dsl, this is kind of trivial
(let [path {\^ (fn [[x y]] [x (inc y)])
            \v (fn [[x y]] [x (dec y)])
            \> (fn [[x y]] [(inc x) y])
            \< (fn [[x y]] [(dec x) y])}]
  (count (distinct (reductions (fn [loc next] ((path next) loc)) [0 0] route))))
  
; part 2 called for a spot of refactoring

(def path-dsl {\^ (fn [[x y]] [x (inc y)])
               \v (fn [[x y]] [x (dec y)])
               \> (fn [[x y]] [(inc x) y])
               \< (fn [[x y]] [(dec x) y])} )

(defn santa-walk 
  [start route]
  (reductions (fn [loc next] ((path-dsl next) loc)) start route))
  
;now a little mental jump, since orders to delivery drones are interleaved you can in fact think of them as pairs of orders
; then from each pair of orders mapping to drones is positional within a pair

(defn two-drones
  [route]
  (let [pairs (partition 2 route)
        distill (comp count distinct concat)        
        walk-from-0 (partial santa-walk [0 0])]     ;inject starting point
    (distill (walk-from-0 (map first pairs))
             (walk-from-0 (map last pairs)))))

;this could be generalized event further:

(defn walk-drones
   [num-drones route]
   (let [ordersets (partition num-drones route)
      ... etc ...
     
;;cgrand pointed out that there's more than one way to partition things
(defn two-drones
  [route]
  (let [distill (comp count distinct concat)
        walk-from-0 (partial santa-walk [0 0])
        walk-second (fn [coll] (walk-from-0 (take-nth 2 coll)))]
    (distill (walk-second route)
             (walk-second (rest route)))))
