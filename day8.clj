(ns adventofcode.day8)

;; slurp the instructions
(def instr (clojure.string/split-lines (slurp "path to file")))

;;the simple way is to just count characters:
(- (reduce + (map (comp count seq) instr))
   (reduce +
           (map #(-> %
                     (clojure.string/replace #"\\[\\|\"]" "0") ;replace \\ & \" with single innocuous character
                     (clojure.string/replace #"\\x\w\w" "0")   ;replace escaped char with single char
                     seq
                     count
                     (- 2)) instr)))


;; or you can think of it in positive terms
(+ (* 2 (count instr))                                      ;each string contributes 2 extra chars
   (reduce + (map #(count (re-seq #"\\[\\|\"]" %)) instr))  ;\\ & \" contribute one
   (* 3 (reduce + (map #(count (re-seq #"\\x\w\w" (clojure.string/replace % #"\\\\" ""))) instr)))) ;; escaped chars contribute 3


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(- (reduce + (map (comp count seq pr-str) instr))
   (reduce + (map (comp count seq) instr)))
