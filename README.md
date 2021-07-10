# RulesGeneration
This Minecraft plugin allows you to generate terrain using 16x16x16 chunks that piece together using rules

More precisely, you can have specific rules, like "grass can only generate above stone" (where "grass" and
"stone" are two [CubicChunk](src/main/java/fr/bananasmoothii/rulesgeneration/chunks/CubicChunk.java)
(16x16x16 minecraft blocs); or "stone needs to have at least 1 stone around".

For now, I only get StackOverflowExceptions, ArrayIndexOutOfBoundsExceptions and program never ending...
