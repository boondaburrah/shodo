(ns shodo.core
    (:require [reagent.core :as r :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]))

;; -------------------------
;; Models

(defonce the-list (r/atom (vector)))

(defrecord todo [checked body])

;; -------------------------
;; Views

(defn home-page []
  [:div
   [:h1 "todos"]])

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


;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
