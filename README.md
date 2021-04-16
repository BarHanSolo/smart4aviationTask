# Application to read data about flight realted entities

The application has 3 functionalities.

## For requested Flight Number and date will respond with

- Cargo Weight for requested Flight
- Baggage Weight for requested Flight
- Total Weight for requested Flight


## For requested IATA Airport Code and date will respond with

- Number of flights departing from this airport
- Number of flights arriving to this airport
- Total number (pieces) of baggage arriving to this airport
- Total number (pieces) of baggage departing from this airport

## Options

User can choose between two units of weight to display the cargo weight (kg/lb).

## Data
Data was generated with: https://www.json-generator.com/
There are two entities of data:

### Flight Entity
```javascript
[
'{{repeat(5)}}',
  {
  flightId: '{{index()}}',
  flightNumber: '{{integer(1000, 9999)}}',
  departureAirportIATACode: '{{random("SEA","YYZ","YYT","ANC","LAX")}}',
  arrivalAirportIATACode: '{{random("MIT","LEW","GDN","KRK","PPX")}}',
  departureDate: '{{date(new Date(2014, 0, 1), new Date(), "YYYY-MM-ddThh:mm:ss Z")}}'
  }
]
```

### Cargo Entity
```javascript
[
'{{repeat(5)}}',
  {
  flightId: '{{index()}}',
  baggage: [
  '{{repeat(3,8)}}',
    {
      id: '{{index()}}',
      weight: '{{integer(1, 999)}}',
      weightUnit: '{{random("kg","lb")}}',
      pieces: '{{integer(1, 999)}}'
    }
    ],
  cargo: [
  '{{repeat(3,5)}}',
    {
    id: '{{index()}}',
    weight: '{{integer(1, 999)}}',
    weightUnit: '{{random("kg","lb")}}',
    pieces: '{{integer(1, 999)}}'
    }
  ]
  }
]
```
