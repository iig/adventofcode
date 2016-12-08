(ns adventofcode.day11)

;; let's make some filters for prohibited characters that return the next character
;; or return the input
(defn rule2-filter
  [i]
  (-> (filter (complement nil?)
              ((apply juxt
                      (map #(fn [x] (when (= x %) (inc x))) (map int "iol")))
                i))
      (conj i)
      last))

;; given a character, the function increments it applying rule 2 and doing wrap-around
(defn get-next
  [c]
  (let [i (-> c
              int
              inc
              rule2-filter)]
    (char (if (= i 123)
            97
            i))))

;;this will overflow out the left end of the string
;; recursively increments the characters in the string
(defn cincr
  [chs]
  (let [t (get-next (last chs))
        f (if (= \a t)
            (cincr (butlast chs))
            (butlast chs))]
    (conj (vec f) t)))

;; a version that will output strings
(def sincr (comp (partial apply str) cincr))

;; now let's build the regex for increasing combinations
(def increasing (->> (range 97 123)                         ;;lowercase letters
                     (map char)
                     (partition 3 1)                        ;; split in triplepts 
                     (map (partial apply str))              ;; turn into strings
                     (filter #(nil? (re-find #"[ilo]" %)))  ;; remove any that contain prohibited chars
                     (interpose "|")                        ;; put regex or between each
                     ((partial apply str))
                     (#(str "(" % ")"))                     ;; regex group
                     re-pattern))                           ;; compile regex

;; the second regex is basically lookbehinds
(def two-pairs #"(\w)\1.*(\w)\2")

;; package the regexes
(def check-regex-rules
  #(every? (complement nil?)
           ((juxt (partial re-find increasing) (partial re-find two-pairs)) %)))

;;now let's put it all together
(defn increment-password
  [password]
  (let [nxt (sincr password)]    ;make next password
    (if (check-regex-rules nxt)  ;check if it's valid
      nxt                        ;return
      (recur nxt))))             ;or rinse/repeat. notice the recur. Oh, the JVM...

;; for part two you just do
 (-> "password"
     increment-password
     increment-password)
