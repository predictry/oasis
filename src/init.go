package oms

import (
	"fmt"
	"html"
	"log"
	"net/http"
)

// ok this is the start of something
// life is what you define
// good or bad ?

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Hello, %q", html.EscapeString(r.URL.Path))
	})

	log.Fatal(http.ListenAndServe(":3000", nil))
}
