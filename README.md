# ESON
### An Easy Java JSON parser

The goal of ESON is to be a simple and easy to use JSON parser. Nothing more, nothing less.

##Example:

Here's a basic JSON file:

```
{
  "person" : {
    "firstName" : "Billy",
    "lastName" : "Bob",
    "age" : 47,
    "midLifeCrisis" : true,
    "cars" : [
      {"make" : "Ford", "model" : "Mustang", "year" : 2005},
      {"make" : "Ford", "model" : "Pinto", "year" : "Before Time"}
    ]
  }
}
```
To start parsing this JSON data you can do one of two things:

```
String pathToJson = "/path/to/json/file.json"
JSONEntry root = JSONEntry.parseJSON(pathToJson, true); 
//the boolean parameter tells parseJSON if you are passing in a file path or raw JSON data
```

Alternatively:

```
JSONEntry root = JSONEntry.parseJSON(jsonData, false);
//jsonData being a String that follows the JSON file format
```

Once you've used either of these methods to get yourself a JSONEntry object you're ready to start parsing your JSON data.
Here's an example of how you might do that.

```
JSONEntry person = root.getEntry("person");
//We'll do a check to make sure the person entry exists and is not null
//It isn't necissarry but it is recommended.
if (person.isDefined()) {
  //Here we won't check to make sure the types are correct and values are assumed to be not null.
  String firstName = person.getEntry("firstName").asString();
  String lastName = person.getEntry("lastName").asString();
  int age = person.getEntry("age").asInt();
  boolean midLifeCrisis = person.getEntry("midLifeCrisis").asBoolean();
  JSONEntry[] cars = person.getEntry("cars").asJSONEntryArray();

  for(JSONEntry car : cars) {
    //We'll just print out our cars
    String make = car.getEntry("make").asString();
    String model = car.getEntry("model").asString();
    //Since in our JSON example one car has a String for the year and another has a number
    //we'll do a check and store the result in an Object. If you want to avoid calling getEntry for
    //the same key twice, you should consider storing car.getEntry("year") int a JSONEntry, but we won't here.
    Object year = car.getEntry("year").isNumber ? car.getEntry("year").asInt() : car.getEntry("year").asString();

    System.out.println("Make: " + make + ", Model: " + model + ", Year: " + year.toString());
  }
}
```

Now that entire JSON file has been parsed.
Of course if you only want to get specific values from a JSON file, you can do that too:

```
  String value = root.getEntry("person").getEntry("cars").getArrayEntry(1).getEntry("year").asString();
  System.out.println(value);
```

If you request an entry that doesn't exist, it will be considered "Undefined" and could throw a null pointer or other
exceptions if used. So it's best to check if the variable is defined before using it.
```
  JSONEntry entry = root.getEntry("potato");
  if(entry.isDefined()) {
    System.out.println(entry.asString());
  } else {
    System.out.println("No potatoes here!");
  }
```