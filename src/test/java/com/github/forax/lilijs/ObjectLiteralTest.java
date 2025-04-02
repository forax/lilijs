package com.github.forax.lilijs;


public class ObjectLiteralTest {
  /*
  private static String execute(String code) {
    var outStream = new ByteArrayOutputStream(8192);
    var printStream = new PrintStream(outStream, false, UTF_8);
    Interpreter.interpret(code, printStream);
    return outStream.toString(UTF_8).replace("\r\n", "\n");
  }

  @Test
  public void testBasicObjectLiteral() {
    assertEquals("John\n30\n", execute("""
        const person = {
            name: "John",
            age: 30
        };
        print(person.name);
        print(person.age);
        """));
  }

  @Test
  @Disabled
  public void testEmptyObjectLiteral() {
    assertEquals("object\n{}\n", execute("""
        const emptyObj = {};
        print(typeof emptyObj);
        print(JSON.stringify(emptyObj));
        """));
  }

  @Test
  public void testNestedObjectLiterals() {
    assertEquals("John\nNew York\n", execute("""
        const person = {
            name: "John",
            address: {
                city: "New York",
                zip: 10001
            }
        };
        print(person.name);
        print(person.address.city);
        """));
  }

  @Test
  public void testObjectLiteralWithMethods() {
    assertEquals("Hello, John!\n", execute("""
        const person = {
            name: "John",
            greet: function() {
                print("Hello, " + this.name + "!");
            }
        };
        person.greet();
        """));
  }

  @Test
  public void testObjectLiteralWithMethodShorthand() {
    assertEquals("Hello, John!\n", execute("""
        const person = {
            name: "John",
            greet() {
                print("Hello, " + this.name + "!");
            }
        };
        person.greet();
        """));
  }

  @Test
  @Disabled
  public void testObjectLiteralWithComputedProperties() {
    assertEquals("value1\nvalue2\n", execute("""
        const propName1 = "key1";
        const propName2 = "key2";
        
        const obj = {
            [propName1]: "value1",
            [propName2]: "value2"
        };
        
        print(obj.key1);
        print(obj.key2);
        """));
  }

  @Test
  @Disabled
  public void testObjectLiteralWithComplexComputedProperties() {
    assertEquals("value3\n", execute("""
        const prefix = "prefix";
        
        const obj = {
            [prefix + "_key"]: "value3"
        };
        
        print(obj.prefix_key);
        """));
  }

  @Test
  @Disabled
  public void testObjectPropertyShorthand() {
    assertEquals("John\n30\n", execute("""
        const name = "John";
        const age = 30;
        
        const person = {
            name,
            age
        };
        
        print(person.name);
        print(person.age);
        """));
  }

  @Test
  public void testMixedPropertyTypes() {
    assertEquals("John\n30\nNew York\n", execute("""
        const person = {
            name: name,
            age: 30,
            city: "New York"
        };
        
        print(person.name);
        print(person.age);
        print(person.city);
        """));
  }

  @Test
  @Disabled
  public void testObjectWithArrayValues() {
    assertEquals("red\ngreen\nblue\n", execute("""
        const palette = {
            colors: ["red", "green", "blue"]
        };
        
        print(palette.colors[0]);
        print(palette.colors[1]);
        print(palette.colors[2]);
        """));
  }

  @Test
  @Disabled
  public void testObjectWithNestedArrayAndObjects() {
    assertEquals("red\nNew York\nsushi\n", execute("""
        const person = {
            name: "John",
            favorites: {
                colors: ["red", "blue", "green"],
                places: [
                    {city: "New York", visited: 2020},
                    {city: "London", visited: 2018}
                ],
                food: "sushi"
            }
        };
        
        print(person.favorites.colors[0]);
        print(person.favorites.places[0].city);
        print(person.favorites.food);
        """));
  }

  @Test
  @Disabled
  public void testObjectSpreadOperator() {
    assertEquals("John\n30\ntrue\n", execute("""
        const basicInfo = {
            name: "John",
            age: 30
        };
        
        const extendedInfo = {
            ...basicInfo,
            isEmployed: true
        };
        
        print(extendedInfo.name);
        print(extendedInfo.age);
        print(extendedInfo.isEmployed);
        """));
  }

  @Test
  @Disabled
  public void testObjectSpreadOverwriting() {
    assertEquals("John\n35\n", execute("""
        const basicInfo = {
            name: "John",
            age: 30
        };
        
        const updatedInfo = {
            ...basicInfo,
            age: 35
        };
        
        print(updatedInfo.name);
        print(updatedInfo.age);
        """));
  }

  @Test
  @Disabled
  public void testMultipleObjectSpreads() {
    assertEquals("John\n35\ntrue\nNYC\n", execute("""
        const basicInfo = {
            name: "John",
            age: 30
        };
        
        const employmentInfo = {
            isEmployed: true,
            title: "Developer"
        };
        
        const locationInfo = {
            city: "NYC",
            country: "USA"
        };
        
        const person = {
            ...basicInfo,
            ...employmentInfo,
            ...locationInfo,
            age: 35 // Overwrites the age from basicInfo
        };
        
        print(person.name);
        print(person.age);
        print(person.isEmployed);
        print(person.city);
        """));
  }

  @Test
  @Disabled
  public void testObjectPropertyAccessors() {
    assertEquals("John\n31\n", execute("""
        const person = {
            _name: "John",
            _age: 30,
            get name() {
                return this._name;
            },
            set name(value) {
                this._name = value;
            },
            get age() {
                return this._age;
            },
            set age(value) {
                if (value > 0) {
                    this._age = value;
                }
            }
        };
        
        print(person.name);
        person.age = 31;
        print(person.age);
        """));
  }

  @Test
  @Disabled
  public void testPropertyAccessorValidation() {
    assertEquals("30\n", execute("""
        const person = {
            _age: 30,
            get age() {
                return this._age;
            },
            set age(value) {
                if (value > 0) {
                    this._age = value;
                }
            }
        };
        
        person.age = -10; // Should be rejected
        print(person.age); // Should still be 30
        """));
  }

  @Test
  @Disabled
  public void testObjectWithSymbolKeys() {
    assertEquals("John\nsymbol value\n", execute("""
        const nameSymbol = Symbol("name");
        
        const person = {
            name: "John",
            [nameSymbol]: "symbol value"
        };
        
        print(person.name);
        print(person[nameSymbol]);
        """));
  }

  @Test
  @Disabled
  public void testDynamicPropertyAccess() {
    assertEquals("John\n30\n", execute("""
        const person = {
            name: "John",
            age: 30
        };
        
        const prop1 = "name";
        const prop2 = "age";
        
        print(person[prop1]);
        print(person[prop2]);
        """));
  }

  @Test
  @Disabled
  public void testObjectPropertyExistence() {
    assertEquals("true\nfalse\n", execute("""
        const person = {
            name: "John",
            age: 30
        };
        
        print("name" in person);
        print("address" in person);
        """));
  }

  @Test
  @Disabled
  public void testObjectOwnPropertyCheck() {
    assertEquals("true\nfalse\ntrue\n", execute("""
        const person = {
            name: "John",
            age: 30
        };
        
        print(person.hasOwnProperty("name"));
        print(person.hasOwnProperty("toString"));
        print(Object.prototype.hasOwnProperty.call(person, "age"));
        """));
  }

  @Test
  @Disabled
  public void testObjectEnumeration() {
    assertEquals("name,age\n", execute("""
        const person = {
            name: "John",
            age: 30
        };
        
        const keys = Object.keys(person);
        print(keys);
        """));
  }

  @Test
  @Disabled
  public void testObjectValues() {
    assertEquals("John,30\n", execute("""
        const person = {
            name: "John",
            age: 30
        };
        
        const values = Object.values(person);
        print(values);
        """));
  }

  @Test
  @Disabled
  public void testObjectEntries() {
    assertEquals("name,John,age,30\n", execute("""
        const person = {
            name: "John",
            age: 30
        };
        
        const entries = Object.entries(person);
        // Flattening entries for simple print output
        let result = [];
        for (const [key, value] of entries) {
            result.push(key, value);
        }
        print(result);
        """));
  }

  @Test
  @Disabled
  public void testObjectAssign() {
    assertEquals("John\n31\ntrue\n", execute("""
        const person = {
            name: "John",
            age: 30
        };
        
        const updates = {
            age: 31,
            isEmployed: true
        };
        
        const result = Object.assign({}, person, updates);
        
        print(result.name);
        print(result.age);
        print(result.isEmployed);
        """));
  }

  @Test
  @Disabled
  public void testObjectFreeze() {
    // This test should show an error in strict mode or silently fail in non-strict mode
    String result = execute("""
        "use strict";
        const person = {
            name: "John",
            age: 30
        };
        
        Object.freeze(person);
        
        try {
            person.age = 31; // Should throw in strict mode
            print("No error: " + person.age); // Should still be 30 even if no error
        } catch (e) {
            print("Error caught when modifying frozen object");
        }
        """);

    // Either we get an error message or the property didn't change
    assertTrue(result.contains("Error caught") || result.contains("No error: 30"));
  }

  @Test
  @Disabled
  public void testObjectSeal() {
    assertEquals("John\n31\nError caught when adding property\n", execute("""
        "use strict";
        const person = {
            name: "John",
            age: 30
        };
        
        Object.seal(person);
        
        // Can modify existing properties
        person.age = 31;
        print(person.name);
        print(person.age);
        
        // Cannot add new properties
        try {
            person.isEmployed = true;
            print("No error, but property should not be added: " + person.isEmployed);
        } catch (e) {
            print("Error caught when adding property");
        }
        """));
  }

  @Test
  @Disabled
  public void testObjectPrototype() {
    assertEquals("John\nreference\n", execute("""
        function Person(name) {
            this.name = name;
        }
        
        Person.prototype.type = "reference";
        
        const person = new Person("John");
        print(person.name);
        print(person.type);
        """));
  }

  @Test
  @Disabled
  public void testObjectCreateWithPrototype() {
    assertEquals("reference\nJohn\n", execute("""
        const personProto = {
            type: "reference",
            greet() {
                return "Hello, " + this.name;
            }
        };
        
        const person = Object.create(personProto);
        person.name = "John";
        
        print(person.type);
        print(person.name);
        """));
  }

  @Test
  @Disabled
  public void testObjectWithGetOwnPropertyDescriptor() {
    assertEquals("true\nfalse\ntrue\n", execute("""
        const person = {
            name: "John"
        };
        
        Object.defineProperty(person, "age", {
            value: 30,
            enumerable: false,
            writable: true,
            configurable: true
        });
        
        const nameDesc = Object.getOwnPropertyDescriptor(person, "name");
        const ageDesc = Object.getOwnPropertyDescriptor(person, "age");
        
        print(nameDesc.enumerable);
        print(ageDesc.enumerable);
        print(ageDesc.writable);
        """));
  }
  */
}