# lilijs
A Java runtime for a restricted set of TypeScript

## A runtime specalized for the backend

lilijs is a runtime engine dedicated to run a subset of TypeScript fast on the backend.

On the backend side, more and more people are using TypeScript to typecheck their application. 

TypeScript has a stricter semantics than JavaScript/EcmaScript, by example, this snippet
is valid in JavaScript but not in TypeScript which requires the left part to be either a number (or a BigInt)
```typescript
  {} + 3
```


The idea behind lilijs is that we can run code faster on the backend side than
popular engine like V8 (chrome), JavascriptCore (Safari) or SpiderMonkey (firefox)
because
- we only run TypeScript
- we only run a subset of TypeScript carefully chosen (named "TypeScript the good part") so running it is fast
- we run on top of the Java Virtual Machine, so startup will not be fast (still less than 1s)
  but we should have better peak performance than popular JavaScript engines.

lilijs has an integrated linter based on swc that you can run separately if you want to check that your code
is compatible with "TypeScript the good part".
If your code is not compatible, lilijs will throw an Error at some point at runtime.


## TypeScript the good part

Here are a description of the restrictions to TypeScript lilijs uses.

### any
`any` is not allowed

### enums and other TypeScript features that requires a runtime


### variable
- no var
- 'let' and `const` only support reading a binding, not writing one so less memory leaks,
  and no [Temporal Dead Zone](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/let#temporal_dead_zone_tdz) for captured variables.

### function declaration
- local functions are defined as `let` instead of `var`
    - you can not access a local function value before its definition
      The following code is not valid with lilijs.
      ```javascript
      function foo() {
        f = 5;
        function f() { } 
      }
      ```
    - you can not redeclare a local function
      ```javascript
      function foo() {
        function f() { }
        function f() { }
      }
      ```
- async functions are supported but the returned `Promise` is always completed (or on failure).
  The state not completed/in flight does not exist.
  In terms of implementation, we rely on the virtual threads (think go routine) of the Java Platform.

- generators are not supported, we do not know how to make them fast, this may change in the future.

### throwing/catching error
- you can not catch errors, errors are exceptional paths
- you can still use Promise as a way to react to errors

### class
We do not support inheritance and super calls, you can use TypeScript interface instead.

### BigInt
We do not support BigInt at the moment, this restriction may be lifted in the future



