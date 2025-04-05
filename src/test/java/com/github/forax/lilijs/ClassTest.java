package com.github.forax.lilijs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class ClassTest {
  private static String execute(String code) {
    var outStream = new ByteArrayOutputStream(8192);
    var printStream = new PrintStream(outStream, false, UTF_8);
    Interpreter.interpret(code, printStream);
    return outStream.toString(UTF_8).replace("\r\n", "\n");
  }

  @Test
  public void testEmptyClass() {
    execute("""
        class Empty {}
        
        let obj = new Empty();
        """);
  }

  @Test
  public void testClassPropertyAssignment() {
    var result = execute("""
        class User {
            name;
        }
        
        let user = new User();
        user.name = "Bob";
        print(user.name);
        """);

    assertEquals("Bob\n", result);
  }

  @Test
  public void testStringClassPropertyAssignment() {
    var result = execute("""
        class User {
            name: string;
        }
        
        let user = new User();
        user.name = "Bob";
        print(user.name);
        """);

    assertEquals("Bob\n", result);
  }

  @Test
  public void testBooleanPropertyAssignment() {
    String result = execute("""
        class Setting {
            enabled: boolean;
        }
        
        let setting = new Setting();
        setting.enabled = true;
        print(setting.enabled);
        
        setting.enabled = false;
        print(setting.enabled);
        """);

    assertEquals("true\nfalse\n", result);
  }

  @Test
  public void testNumberPropertyAssignment() {
    var result = execute("""
        class Counter {
            value: number;
        }
        
        let counter = new Counter();
        counter.value = 42;
        print(counter.value);
        
        counter.value = 3.14;
        print(counter.value);
        
        counter.value = -10;
        print(counter.value);
        """);

    assertEquals("42\n3.14\n-10\n", result);
  }

  @Test
  public void testFunctionPropertyAssignment() {
    var result = execute("""
       class Processor {
           transform: (input: number) => number;
       }
       
       let processor = new Processor();
       
       // Assign arrow function
       processor.transform = (x) => x * 2;
       print(processor.transform(5));
       """);

    assertEquals("10\n", result);
  }

  @Test
  public void testFieldInitializedToUndefined() {
    var result = execute("""
        class Person {
            name;
        }
        
        let person = new Person();
        print(person.name);
        """);

    assertEquals("undefined\n", result);
  }

  @Test
  public void testMethodCall() {
    var result = execute("""
        class Person {
            hello() {
              print("hello");
            }
        }
        
        let person = new Person();
        person.hello();
        """);

    assertEquals("hello\n", result);
  }

  @Test
  public void testMixedPropertyTypes() {
    var result = execute("""
        class UserProfile {
            name: string;
            age: number;
            isActive: boolean;
            getDescription: () => string;
        }

        let profile = new UserProfile();
        profile.name = "Alice";
        profile.age = 30;
        profile.isActive = true;
        profile.getDescription = function() {
            return this.name + ", " + this.age + ", " + (this.isActive ? "active" : "inactive");
        };

        print(profile.name);
        print(profile.age);
        print(profile.isActive);
        print(profile.getDescription());
    """);

    assertEquals("Alice\n30\ntrue\nAlice, 30, active\n", result);
  }

  @Test
  public void testFunctionPropertyWithThis() {
    var result = execute("""
        class Calculator {
            baseValue: number;
            operation: (input: number) => number;
        }
        
        let calc = new Calculator();
        calc.baseValue = 100;
        
        // Function using this context
        calc.operation = function(x) {
            return this.baseValue + x;
        };
        
        print(calc.operation(50));
        
        // Change base value
        calc.baseValue = 200;
        print(calc.operation(50));
    """);

    assertEquals("150\n250\n", result);
  }

  @Test @Disabled  // FIXME
  public void testMultipleInstances() {
    var result = execute("""
        class Counter {
            count: number = 0;
        
            increment() {
                this.count++;
            }
        
            getCount() {
                return this.count;
            }
        }
        
        let c1 = new Counter();
        let c2 = new Counter();
        
        c1.increment();
        c1.increment();
        c2.increment();
        
        print(`c1: ${c1.getCount()}, c2: ${c2.getCount()}`);
        """);

    assertEquals("c1: 2, c2: 1\n", result);
  }

  @Test @Disabled  // FIXME
  public void testMethodChaining() {
    var result = execute("""
        class StringBuilder {
            content: string = "";
        
            append(text: string): StringBuilder {
                this.content = this.content + text;
                return this;
            }
        
            toString(): string {
                return this.content;
            }
        }
        
        let builder = new StringBuilder();
        let result = builder.append("Hello").append(" ").append("World").toString();
        print(result);
        """);

    assertEquals("Hello World\n", result);
  }

  @Test
  public void testFunctionPropertyCallbacks() {
    var result = execute("""
        class EventEmitter {
            onEvent: (data: string) => void;
            
            triggerEvent(data: string): void {
                if (this.onEvent) {
                    this.onEvent(data);
                }
            }
        }
        
        let emitter = new EventEmitter();
        
        emitter.onEvent = data => {
            print(data);
        };
        
        emitter.triggerEvent("hello");
        emitter.triggerEvent("world");
    """);

    assertEquals("hello\nworld\n", result);
  }

  @Test @Disabled  // FIXME
  public void testDefaultConstructor() {
    var result = execute("""
        class DefaultTest {
            name: string = "default";
        
            sayHello() {
                return `Hello, ${this.name}!`;
            }
        }
        
        let obj = new DefaultTest();
        print(obj.sayHello());
        """);

    assertEquals("Hello, default!\n", result);
  }

  @Test
  public void testConstructorClassCreation() {
    var result = execute("""
        class Person {
            name: string;
            age: number;
        
            constructor(name: string, age: number) {
                this.name = name;
                this.age = age;
            }
        
            describe(): string {
                return this.name + " is " + this.age + " years old";
            }
        }
        
        let person = new Person("Alice", 30);
        print(person.describe());
        """);

    assertEquals("Alice is 30 years old\n", result);
  }

  @Test @Disabled
  public void testBooleanOperations() {
    var result = execute("""
        class LogicGate {
            inputA: boolean;
            inputB: boolean;
            
            constructor(a: boolean, b: boolean) {
                this.inputA = a;
                this.inputB = b;
            }
            
            and(): boolean {
                return this.inputA && this.inputB;
            }
            
            or(): boolean {
                return this.inputA || this.inputB;
            }
            
            not(): boolean {
                return !this.inputA;
            }
        }
        
        let gate1 = new LogicGate(true, false);
        print("AND: " + gate1.and());
        print("OR: " + gate1.or()));
        print("NOT: " + gate1.not()));
        
        let gate2 = new LogicGate(true, true);
        print"AND: " + gate2.and());
    """);

    assertEquals("AND: false\nOR: true\nNOT: false\nAND: true\n", result);
  }

  @Test
  public void testNumberOperations() {
    var result = execute("""
        class Vector {
            x: number;
            y: number;

            constructor(x: number, y: number) {
                this.x = x;
                this.y = y;
            }

            add(other: Vector): Vector {
                return new Vector(this.x + other.x, this.y + other.y);
            }

            magnitudeSquare(): number {
                return this.x * this.x + this.y * this.y;
            }
        }

        let v1 = new Vector(3, 4);
        let v2 = new Vector(1, 2);
        let v3 = v1.add(v2);

        print("v3: (" + v3.x + ", " + v3.y + ")");
        print("Magnitude: " + v1.magnitudeSquare());
    """);

    assertEquals("v3: (4, 6)\nMagnitude: 25\n", result);
  }

  @Test @Disabled
  public void testClassInheritance() {
    var result = execute("""
        class Animal {
            name: string;
        
            constructor(name: string) {
                this.name = name;
            }
        
            speak(): string {
                return "Some sound";
            }
        }
        
        class Dog extends Animal {
            breed: string;
        
            constructor(name: string, breed: string) {
                super(name);
                this.breed = breed;
            }
        
            speak(): string {
                return "Woof!";
            }
        
            getInfo(): string {
                return `${this.name} is a ${this.breed}`;
            }
        }
        
        let dog = new Dog("Rex", "German Shepherd");
        print(dog.speak());
        print(dog.getInfo());
        """);

    assertEquals("Woof!\nRex is a German Shepherd\n", result);
  }

  @Test
  public void testClassMethods() {
    var result = execute("""
        class Calculator {
            add(a: number, b: number): number {
                return a + b;
            }
        
            subtract(a: number, b: number): number {
                return a - b;
            }
        
            multiply(a: number, b: number): number {
                return a * b;
            }
        
            divide(a: number, b: number): number {
                if (b === 0) {
                    return 0;
                }
                return a / b;
            }
        }
        
        let calc = new Calculator();
        print(calc.add(5, 3));
        print(calc.subtract(10, 4));
        print(calc.multiply(3, 7));
        print(calc.divide(20, 5));
        """);

    assertEquals("8\n6\n21\n4\n", result);
  }

  @Test @Disabled
  public void testClassProperties() {
    var result = execute("""
        class Rectangle {
            width: number;
            height: number;
        
            constructor(width: number, height: number) {
                this.width = width;
                this.height = height;
            }
        
            get area(): number {
                return this.width * this.height;
            }
        
            set size(scale: number) {
                this.width *= scale;
                this.height *= scale;
            }
        }
        
        let rect = new Rectangle(5, 10);
        print(`Initial area: ${rect.area}`);
        
        rect.size = 2;
        print(`After scaling: ${rect.area}`);
        """);

    assertEquals("Initial area: 50\nAfter scaling: 200\n", result);
  }

  @Test @Disabled
  public void testPrivateMembers() {
    var result = execute("""
        class BankAccount {
            private balance: number;
        
            constructor(initialBalance: number) {
                this.balance = initialBalance;
            }
        
            deposit(amount: number): void {
                if (amount > 0) {
                    this.balance += amount;
                }
            }
        
            withdraw(amount: number): boolean {
                if (amount > 0 && this.balance >= amount) {
                    this.balance -= amount;
                    return true;
                }
                return false;
            }
        
            getBalance(): number {
                return this.balance;
            }
        }
        
        let account = new BankAccount(1000);
        account.deposit(500);
        let withdrawSuccess = account.withdraw(200);
        
        print(`Withdrawal successful: ${withdrawSuccess}`);
        print(`Current balance: ${account.getBalance()}`);
        """);

    assertEquals("Withdrawal successful: true\nCurrent balance: 1300\n", result);
  }

  @Test @Disabled
  public void testStaticMembers() {
    var result = execute("""
        class MathUtils {
            static PI: number = 3.14159;
        
            static square(x: number): number {
                return x * x;
            }
        
            static cube(x: number): number {
                return x * x * x;
            }
        }
        
        print(`PI: ${MathUtils.PI}`);
        print(`Square of 4: ${MathUtils.square(4)}`);
        print(`Cube of 3: ${MathUtils.cube(3)}`);
        """);

    assertEquals("PI: 3.14159\nSquare of 4: 16\nCube of 3: 27\n", result);
  }

  @Test @Disabled
  public void testInterfaceImplementation() {
    var result = execute("""
        interface Shape {
            calculateArea(): number;
            calculatePerimeter(): number;
        }
        
        class Circle implements Shape {
            radius: number;
        
            constructor(radius: number) {
                this.radius = radius;
            }
        
            calculateArea(): number {
                return 3.14 * this.radius * this.radius;
            }
        
            calculatePerimeter(): number {
                return 2 * 3.14 * this.radius;
            }
        }
        
        class Square implements Shape {
            side: number;
        
            constructor(side: number) {
                this.side = side;
            }
        
            calculateArea(): number {
                return this.side * this.side;
            }
        
            calculatePerimeter(): number {
                return 4 * this.side;
            }
        }
        
        let circle = new Circle(5);
        let square = new Square(4);
        
        print(`Circle area: ${circle.calculateArea()}`);
        print(`Square perimeter: ${square.calculatePerimeter()}`);
        """);

    assertEquals("Circle area: 78.5\nSquare perimeter: 16\n", result);
  }

  @Test @Disabled
  public void testClassWithArrayProperty() {
    var result = execute("""
        class Collection {
            items: number[] = [];
        
            add(item: number) {
                this.items.push(item);
            }
        
            getItems() {
                return this.items;
            }
        }
        
        let collection = new Collection();
        collection.add(1);
        collection.add(2);
        collection.add(3);
        
        print(collection.getItems());
        """);

    assertEquals("[1,2,3]\n", result);
  }

  @Test @Disabled
  public void testSimpleInheritance() {
    var result = execute("""
        class Parent {
            getValue() {
                return "parent";
            }
        }
        
        class Child extends Parent {}
        
        let child = new Child();
        print(child.getValue());
        """);

    assertEquals("parent\n", result);
  }

  @Test @Disabled
  public void testConstructorWithDefaults() {
    var result = execute("""
        class Greeter {
            greeting: string;
        
            constructor(greeting: string = "Hello") {
                this.greeting = greeting;
            }
        
            greet(name: string) {
                return `${this.greeting}, ${name}!`;
            }
        }
        
        let defaultGreeter = new Greeter();
        let customGreeter = new Greeter("Hi");
        
        print(defaultGreeter.greet("World"));
        print(customGreeter.greet("TypeScript"));
        """);

    assertEquals("Hello, World!\nHi, TypeScript!\n", result);
  }

  @Test @Disabled
  public void testNestedClassInstantiation() {
    var result = execute("""
        class Outer {
            createInner() {
                class Inner {
                    getValue() {
                        return "inner value";
                    }
                }
        
                return new Inner();
            }
        }
        
        let outer = new Outer();
        let inner = outer.createInner();
        print(inner.getValue());
        """);

    assertEquals("inner value\n", result);
  }

  @Test @Disabled
  public void testClassWithOptionalProperties() {
    var result = execute("""
        class Optional {
            required: string;
            optional?: string;
        
            constructor(required: string, optional?: string) {
                this.required = required;
                this.optional = optional;
            }
        
            describe() {
                if (this.optional) {
                    return `${this.required} (${this.optional})`;
                } else {
                    return this.required;
                }
            }
        }
        
        let withOptional = new Optional("Main", "Extra");
        let withoutOptional = new Optional("Main");
        
        print(withOptional.describe());
        print(withoutOptional.describe());
        """);

    assertEquals("Main (Extra)\nMain\n", result);
  }
}