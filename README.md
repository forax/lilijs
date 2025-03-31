# A TypeScript Runtime Specialized for the server

Lilijs is a runtime engine dedicated to running a subset of TypeScript efficiently on server.

## TypeScript on the Backend

On the backend side, an increasing number of developers are adopting TypeScript to typecheck their applications.
TypeScript enforces stricter semantics compared to JavaScript/ECMAScript.
For instance, this snippet `{} + 3` is valid in JavaScript but rejected by TypeScript,
which requires the left operand to be either a number or a BigInt:

## The Lilijs Approach

The core concept behind LiliJS is achieving superior performance compared to popular engines like V8 (Chrome),
JavaScriptCore (Safari), or SpiderMonkey (Firefox) by:

1. Focusing exclusively on TypeScript execution
2. Supporting only a carefully selected subset of TypeScript (dubbed "TypeScript the Good Parts")
   optimized for performance
3. Running on the Java Virtual Machine, which trades slightly longer startup times (still under 1 second)
   for enhanced peak performance compared to mainstream JavaScript engines

## Compatibility Verification

LiliJS includes an integrated linter based on SWC that can be run independently to verify
your code's compatibility with "TypeScript the Good Parts."
If your code falls outside this subset, LiliJS will throw a runtime error during the execution.

## TypeScript the good part

This is a kind of "superstrict mode" that does not support syntax/constructions of EcmaScript
that are hard to optimize AND for which there is a better/simpler solution.

Here are a description of the restrictions to TypeScript Lilijs uses.

### any
`any` is not allowed.

### enums and other TypeScript features that requires a runtime
No enum declarations, namespaces and modules with runtime code,
parameter properties in classes, Non-ECMAScript import = and export = assignments.

see [--erasablesyntaxonly](https://www.typescriptlang.org/docs/handbook/release-notes/typescript-5-8.html#the---erasablesyntaxonly-option)
for more information.

### variable
- no `var`
- `let` and `const` only support reading a binding, not writing one so less memory leaks,
  and no [Temporal Dead Zone](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/let#temporal_dead_zone_tdz) for captured variables.

### function declaration
- local functions are defined as `let` instead of `var`
    - you can not access a local function value before its definition
      The following code is not valid with Lilijs.
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

- `arguments` inside a function is not supported (strict mode), uses `...` instead. 

### throwing/catching error
- you can not catch errors, errors are exceptional paths
- you can still use a Promise as a way to react to errors

### class
- inheritance and super calls are not supported, you can use TypeScript interface instead.
- adding a field dynamically is not supported (not compatible with TypeScript).

### BigInt
We do not support BigInt at the moment, this restriction may be lifted in the future


## Tasks

The project has just begun, I hope to finish most of it in one year.

[x] local variables
[x] function, local function, arrow function and calls
[x] basic numeric ops
[x] basic control flow
[] compound operations +=, -=, &=, etc
[] label, break, continue in loop
[] throw exception
[] switch
[] && and ||
[] destructuring
[] function default parameters
[] class
[] private properties
[] static method and static block
[] typeof
[] instanceof
[] field, '.' and '?.' access
[] method calls
[] object literal
[] array literal
[] regex literal
[] async functions and Promise
[] Object & Function
[] Math
[] JSON
[] Errors
[] Symbol ??
[] imports ??

### Optimizations

[x] inlining cache (IC) for function definitions
[x] IC for call
[x] IC for numeric ops
[] more inlining caches :)



