DimensionGuard
==============

Configurable filtering of Items, Blocks and Entities by Dimension

Format
==============
All names are case-insensitive

Blocks and Items
--------------
modid:block/item(:metadata optional),dimension list

Entities
--------------
entity name, dimension list

Wildcards
--------------
For IDs, names and metadata the wildcard \* selects for "any match" so for example "\*spider" will select both Spider and CaveSpider, whereas "spider\*" will only select Spider.

For Dimensions you can define single dimensions (0), ranges (0:5), and use the ++ or -- wildcards to signify more or less than the preceding number (eg 1++)


License
==============

Licensed under GNU GPLv3 found at LICENSE.md
