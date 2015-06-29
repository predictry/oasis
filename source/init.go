package main

import (
	"fmt"
	"math/rand"
)

// ok this is the start of something
// life is what you define
// good or bad ?

func main() {
	r := rand.New(rand.NewSource(9999))
	fmt.Println("Hello Mr", r.Int31(), ".")
}
