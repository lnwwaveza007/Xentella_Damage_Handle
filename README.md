# Xentella_Damage_Handle
Made for DJKlaKunG#4553 

## Commands
- /xdh reload - Reload plugin

## Mythicmobs Mechanics
### XentellaDamage
#Damage to entity
  
| Attribute | Aliases | Description | Default |
| --- | --- | --- | --- |
| amount | a | amount of damage | | 
| damagetype |  | type of damage (MAGIC/PHYSICAL) | | 
| element |  | element of damage (7 Elements) | | 
  
Examples
  
```
testskill1:
  Skills:
  - XentellaDamage{amount=5;damagetype=MAGIC;element=GEO} @Target
```

### ElementShield
#Give Shield of Element to Entity
  
| Attribute | Aliases | Description | Default |
| --- | --- | --- | --- |
| amount | a | amount of shield | | 
| time |  | time of shield (seconds) | | 
| element |  | element (7 Elements) | | 
  
Examples
  
```
testsheild:
  Skills:
  - ElementShield{amount=100;element=HYDRO;time=30} @Self
```

### More Detail Look inside [ Detail.txt ]
