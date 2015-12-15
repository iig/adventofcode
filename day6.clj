;; this will be more of a notebook format.
;; load the instruction file into instructions var

;; let's see how regexes do

(bench (->> (clojure.string/split-lines instructions)
                   (map (partial re-find #"([a-z ]+)\s(\d+),(\d+)[a-z ]+(\d+),(\d+)"))
                   (map (fn [[_ dir x1 y1 x2 y2]] {:dir dir :x1 x1 :y1 y1 :x2 x2 :y2 y2}))))
Evaluation count : 415440 in 60 samples of 6924 calls.
             Execution time mean : 145.435131 µs
    Execution time std-deviation : 889.790359 ns
   Execution time lower quantile : 144.556944 µs ( 2.5%)
   Execution time upper quantile : 147.895166 µs (97.5%)
                   Overhead used : 1.831614 ns

Found 5 outliers in 60 samples (8.3333 %)
	low-severe	 1 (1.6667 %)
	low-mild	 4 (6.6667 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers
 
;; now that we know that, let's see if regexes can be beat (i'm guessing no)

(bench  (->> (clojure.string/split-lines instructions)
             (map #(subs % 5))
             (map #(clojure.string/split % #"\s|,"))
             (map (fn [[dir x1 y1 _ x2 y2]] {:dir dir :x1 x1 :y1 y1 :x2 x2 :y2 y2}))))
Evaluation count : 433680 in 60 samples of 7228 calls.
             Execution time mean : 138.947581 µs
    Execution time std-deviation : 1.501249 µs
   Execution time lower quantile : 137.809316 µs ( 2.5%)
   Execution time upper quantile : 143.671419 µs (97.5%)
                   Overhead used : 1.831614 ns

Found 7 outliers in 60 samples (11.6667 %)
	low-severe	 1 (1.6667 %)
	low-mild	 6 (10.0000 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers

;; and this is why i should never guess
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; then we compose a parsing fuction:

(def parse-instructions (comp #(clojure.string/split % #"\s|,") #(subs % 5)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; and with that generating coordinates is just a matter of a for loop

(defn make-coordinates
  [x1 y1 x2 y2]
  (for [x (range (Integer/parseInt x1) (inc (Integer/parseInt x2))) 
        y (range (Integer/parseInt y1) (inc (Integer/parseInt y2)))] 
    [x y]))
    
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; so the naive solution is below
;; now i will argue, that for this problem, a thing that will have to be run only once
;; this is sufficient, it's simple, it's naive, and while it takes half a minute to run
;; it got the right answer

(def rules {"e"   #(if (= 1 %) 0 1)   ;if you dont' initalize your data structure with 0s you end up with nils and so (= 0 %) would do odd things
            "on"  (fn [_] 1)
            "off" (fn [_] 0)})

(time (reduce + (vals (reduce (fn [board [dir x1 y1 _ x2 y2]]
                                (reduce (fn [board xy] (update board xy (rules dir))) board (make-coordinates x1 y1 x2 y2)))
                              {}
                              (map parse-instructions (clojure.string/split-lines instructions))))))
"Elapsed time: 34739.182164 msecs"
=> 400410

;; and of course all you ahve to do to solve part 2 is to update you rules:
(def init #(if (nil? %) 0 %))
(def rules {"e" (comp inc inc init)
            "on" (comp inc init)
            "off" (comp #(if (zero? %) 0 (dec %)) init)})
            
"Elapsed time: 36868.053991 msecs"
=> 15343601

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ok. that was slow. can we make it faster? Sure let's throw in transients.
;; note how code is becoming uglier as time improves

(def rules {"e"   (fn [board xy] (assoc! board xy (if (= 1 (get board xy)) 0 1)) )
            "on"  #(assoc! %1 %2 1)
            "off" #(assoc! %1 %2 0)})
           
(time (reduce + (vals (persistent! (reduce (fn [board [dir x1 y1 _ x2 y2]]
                                             (reduce (rules dir) board (make-coordinates x1 y1 x2 y2)))
                                           (transient {})
                                           (map parse-instructions (clojure.string/split-lines instructions)))))))
"Elapsed time: 23779.708175 msecs"
=> 400410
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; of course this is not strictly apples to apples. Here's how much transients really give us here:

(def rules {"e"   (fn [board xy] (assoc board xy (if (= 1 (get board xy)) 0 1)) )
            "on"  #(assoc %1 %2 1)
            "off" #(assoc %1 %2 0)})
           
(time (reduce + (vals (reduce (fn [board [dir x1 y1 _ x2 y2]]
                                (reduce (rules dir) board (make-coordinates x1 y1 x2 y2)))
                              {}
                              (map parse-instructions (clojure.string/split-lines instructions)))))
"Elapsed time: 30647.58604 msecs"
=> 400410
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; perhaps if we use vector of vectors and use assoc-in

(def rules {"e"   (fn [board xy] (assoc-in board xy (if (= 1 (get-in board xy)) 0 1)) )
            "on"  #(assoc-in %1 %2 1)
            "off" #(assoc-in %1 %2 0)})
           
;; also since wrapping is getting kinda crazy here let's try for a bit of threading

(->> (map parse-instructions (clojure.string/split-lines instructions))
     (reduce (fn [board [dir x1 y1 _ x2 y2]]
               (reduce (rules dir) board (make-coordinates x1 y1 x2 y2)))
             (into [] (repeat 1000 (into [] (repeat 1000 0))))
             )
     (map (partial reduce +))
     (reduce +)
     time)
"Elapsed time: 16328.904753 msecs"
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ok that's pretty good, but there are a lot of numeric operations here, what if we could avoid boxing?

(->> (map parse-instructions (clojure.string/split-lines instructions))
     (reduce (fn [board [dir x1 y1 _ x2 y2]]
               (reduce (rules dir) board (make-coordinates x1 y1 x2 y2)))
             (into [] (repeat 1000 (into (vector-of :short) (repeat 1000 0))))
             )
     (map (partial reduce +))
     (reduce +)
     time)

"Elapsed time: 16887.392442 msecs"
=> 400410

;; that was strange... overwhelmed by confusion i decided what would happen if i mis-match types ... exception, right?
(def rules {"e"   (fn [board xy] (assoc-in board xy (if (= 1 (get-in board xy)) 0 1)) )
            "on"  #(assoc-in %1 %2 1.1)
            "off" #(assoc-in %1 %2 0.0)})
=> #'user/rules
(->> (map parse-instructions (clojure.string/split-lines instructions))
     (reduce (fn [board [dir x1 y1 _ x2 y2]]
               (reduce (rules dir) board (make-coordinates x1 y1 x2 y2)))
             (into [] (repeat 1000 (into (vector-of :short) (repeat 1000 0))))
             )
     (map (partial reduce +))
     (reduce +)
     time)

"Elapsed time: 16866.603114 msecs"
=> 400410

;; WAT? ok that's really strange, looking at source vector-of pointed to a certain limitation...

(bench (vector-of :int 1 2 3))
Evaluation count : 3137836500 in 60 samples of 52297275 calls.
             Execution time mean : 17.274888 ns
    Execution time std-deviation : 0.846527 ns
   Execution time lower quantile : 16.412557 ns ( 2.5%)
   Execution time upper quantile : 19.282963 ns (97.5%)
                   Overhead used : 1.888324 ns

Found 4 outliers in 60 samples (6.6667 %)
	low-severe	 3 (5.0000 %)
	low-mild	 1 (1.6667 %)
 Variance from outliers : 35.2087 % Variance is moderately inflated by outliers
=> nil
(bench (vector-of :int 1 2 3 4))
Evaluation count : 3228888060 in 60 samples of 53814801 calls.
             Execution time mean : 16.993166 ns
    Execution time std-deviation : 0.827388 ns
   Execution time lower quantile : 16.393315 ns ( 2.5%)
   Execution time upper quantile : 19.282957 ns (97.5%)
                   Overhead used : 1.888324 ns

Found 5 outliers in 60 samples (8.3333 %)
	low-severe	 3 (5.0000 %)
	low-mild	 2 (3.3333 %)
 Variance from outliers : 35.1898 % Variance is moderately inflated by outliers
=> nil
(bench (vector-of :int 1 2 3 4 5))
Evaluation count : 436975800 in 60 samples of 7282930 calls.
             Execution time mean : 141.825161 ns
    Execution time std-deviation : 7.172131 ns
   Execution time lower quantile : 132.951962 ns ( 2.5%)
   Execution time upper quantile : 156.908396 ns (97.5%)
                   Overhead used : 1.888324 ns

Found 1 outliers in 60 samples (1.6667 %)
	low-severe	 1 (1.6667 %)
 Variance from outliers : 36.8370 % Variance is moderately inflated by outliers
=> nil
;; well then, since i'm not going to try to come up with a way to stuff the solution into at most 4 element vectors this is a dead end
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; but i do remember that transients gave quite a bump... so let's try that
(def rules {"e"   (fn [board [x y]] (assoc! (get board x) y (if (= 1 (get (get board x) y)) 0 1)) )
            "on"  (fn [board [x y]] assoc! (get board x) y 1)
            "off" (fn [board [x y]] assoc! (get board x) y 0)})
;; of course this doesn't work really, because with transient you have to use returned value so rules become more complex
;; so rules got ugly:

(def rules {"e"   (fn [board [x y]] (assoc! board x (assoc! (get board x) y (if (= 1 (get (get board x) y)) 0 1))))
            "on"  (fn [board [x y]] (assoc! board x (assoc! (get board x) y 1)))
            "off" (fn [board [x y]] (assoc! board x (assoc! (get board x) y 0)))})
=> #'user/rules
(->> (map parse-instructions (clojure.string/split-lines instructions))
     (reduce (fn [board [dir x1 y1 _ x2 y2]]
               (reduce (rules dir) board (make-coordinates x1 y1 x2 y2)))
             (transient (into [] (map transient (repeat 1000 (into [] (repeat 1000 0))))))
             )
     persistent!
     (map persistent!)
     (map (partial reduce +))
     (reduce +)
     time)
"Elapsed time: 2534.651684 msecs"
=> 400410
;;i'm gona call this enough
