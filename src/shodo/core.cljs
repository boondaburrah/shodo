(ns shodo.core
    (:require [reagent.core :as r :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]))

;; -------------------------
;; Models

(defonce the-list (r/atom (vector)))

(defrecord todo [checked body])

;; -------------------------
;; Controllers

(defn add-todo [text]
  (swap! the-list conj (todo. false text)))

(defn del-todo [id]
  (swap! the-list
         (fn [l i]
           (vec (concat (subvec l 0 i) (subvec l (inc i)))))
         id))

(defn toggle-todo [id]
  (swap! the-list
         (fn [l i]
           (let [me (nth l i)]
             (assoc l i (todo. (not (:checked me)) (:body me)))))
         id))

(defn retext-todo [id new-text]
  (swap! the-list
         (fn [l i t]
           (let [me (nth l i)]
             (assoc l i (todo. (:checked me) t))))
         id
         new-text))

(defn clear-finished-todo []
  (swap! the-list (fn [l] (vector (filter (fn [x] (not (:checked x))) l)))))


;; -------------------------
;; Views

(defn display-todo [id]
  (let [the-todo (nth (deref the-list) id)]
    [:li {:class (str (if (:checked the-todo) "finished" "unfinished"))}
      [:input {:type "checkbox" :checked (:checked the-todo) :on-change (fn [] (toggle-todo id))}]
      [:label (str (:body the-todo))]]))

(defn home-page []
  [:div
   [:h1 "todos"]
   [:ul (map-indexed (fn [id item] (display-todo id)) (deref the-list))]])

;; -------------------------
;; Initialize app

(defonce init
  (do
    (add-todo "Actually write any react stuff")
    (add-todo "get this published somewhere")
    (add-todo "Make subtitleparty")
    (add-todo "Profit?")))

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
