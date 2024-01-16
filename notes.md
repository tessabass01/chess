# My notes

## Built in Object Methods
```
toString() : String

equals() : boolean

hashCode() : int

clone() : Object

wait()

notify()
```

*You always derive from Object Class*

## Quick Starter Code for a Class

`record PetRecord(int id, String name, String Type) {}`

You can write another line of code to override `toString()`. You can use a decorator to make it look nice like `@Override`.

## Creating an Object

Use `var` if you are lazy.

`var x = new PetRecord(24987, 'Sparky', 'dog')`

