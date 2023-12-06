# UniBot - universal chat bot 

## Structure

Entry point is in `Main.kt`

Application lifecycle:
 - Read the environment
 - Instantiate the Application
 - Setup it
 - Database?
 - Multithreading. Coroutines context. Schedulers
 - Logging

Bots lifecycle:
 - Instantiation of all bots
 - Setup them
 - Run

## Application

 - Message handlers:
   - Take a message object. Intent? Message? Request? Action?
     - it might be a command or a message
       - with attachment, usually files. Image or Media(audio/video)
       - might have an emoji-reaction 
       - might be a reply to another message
         - in that case the original message also 
   - Produce a response
     - Find appropriate message handler 
     - Map incoming message to outgoing action: Action? Response? Message?
       - Reply to a user
       - Send status
         - writing
         - sending media
         - done writing

- Settings
  - Define which features enabled and which are not
  - Define thresholds for some responses. e.g. how often they should happen
  - Quotas? for some features. Individually for a chat
  - Bot settings should be only accessible in private messages from admins
  - Some features could be triggered by users e.g. to buy a response/extend quota 
  and so on.