(ns pgtron.create
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [pgtron.docs :as docs]
            [cljs.core.async :refer [>! <!]]
            [chloroform.core :as form]
            [charty.core :as chart]
            [pgtron.style :refer [style icon]]))

(def statemets {
                "database" "CREATE DATABASE <name>
              WITH
             OWNER = <user_name>
          TEMPLATE = <template>
          ENCODING = <encoding>
        LC_COLLATE = <lc_collate>
          LC_CTYPE = <lc_ctype>
        TABLESPACE = <tablespace>
  CONNECTION LIMIT = <connlimit>"

                "extension" "
CREATE EXTENSION
   IF NOT EXISTS '<extension_name>'
   WITH   SCHEMA '<schema_name>'
         VERSION '<version>'
            FROM '<old_version>'
         CASCADE
"
                } )

(defn $index [{object :object db :db :as params}]
  (let [state (atom {:sql (or (get statemets object) (str "CREATE " object))})
        handle (fn [] (println "hi"))]
    (fn []
      [l/layout {:params params :bread-crump [{:title "Create"} {:title (name object)}]}
       [:div#new
        (style [:#new {:display "flex"
                       :height "100%"
                       :outline "1px solid gray"
                       :$color :gray
                       :flex-direction "row"}
                [:h]
                [:.card {:$padding [1 2]}]
                [:#main {:flex-grow 1
                         :flex-basis 0;
                         :outline "1px solid #666"}
                 :.CodeMirror {:height "100%"}]
                [:.docs {:flex-grow 1
                         :flex-basis 0;
                         :border-top "1px solid #666"
                         :$color [:black :white]
                         :$padding [1 2]
                         :overflow-y "scroll"}]
                docs/styles])
        [:div#main
         [form/codemirror state [:sql] {:theme "railscasts"
                                        :mode "text/x-sql"
                                        :extraKeys {"Ctrl-Enter" handle}}]]

        [:div.docs [:div {:dangerouslySetInnerHTML #js{:__html (docs/docs (str "create-" object))}}]]]])))

(def routes {[:object] {:GET #'$index}})
