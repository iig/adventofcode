;;this was too easy...
;; but you need the digest library: https://github.com/tebeka/clj-digest 

(require 'digest)
(reduce (fn [_ i] (when (re-seq #"^[0]{6}" (digest/md5 (str "ckczppom" i))) (reduced i))) nil (range))
                    
; this is a bit ugly so fun it

(defn all-the-md5s
  [key rex]
  (reduce (fn [_ i] 
            (when (re-seq rex (digest/md5 (str key i))) 
              (reduced i))) 
          nil 
          (range)))
          
(all-the-md5s YOURSECRETKEY #"^[0]{5}")          
(all-the-md5s YOURSECRETKEY #"^[0]{6}")
