## Get downed instead of dying so that your friends can revive you!



![Down But Not Out Logo in minecraft-like font](https://cdn.modrinth.com/data/cached_images/84fd8f68e4167ecba7b2c46e6f13f53138dc35f9.png)


To revive your downed friend, simply hold **right click** with an empty hand on them (or **left trigger** on a Geyser Bedrock Client)


### Features:
- Instead of dying, a player is downed with a half of a heart and can be revived by other players
- Downed players have a bleed out timer and die if not revived in time
- Provides and easy to use clickable command "/bleedout" to give up early and respawn
- Fully server side and works with Vanilla clients (only needs to be installed on server)
- Works with Geyser Bedrock clients (with only visual limitations like glowing effects not visible, crawling animation limited, ect.)
- Player's name and glowing effect color progressively turn from yellow to dark red to indicate how long a player has left
- A percentage indicator and aqua color indicate reviving a player with holding right click
- Preserves the original damage source so that death messages and grave headstones are accurate
- Made to be as compatible as possible with other mods
- Penalizes players that log off and log back on while downed, or get downed over and over in quick succession with a shorter bleed out timer
- Interactive sounds that play on revive, downed, and progressive heartbeat sounds during bleedout
- Does not break _respawn anchors_, _death count statistics_, or _totems of undying!_



<details>
<summary>Configuration</summary>
[config location]/DownButNotOut/config.json
  
```json
WARNING: DO NOT COPY AND PASTE THESE COMMENTS, INVALID JSON WILL HALT THE STARTUP.
SIMPLY DELETE THE CONFIG AND RESTART YOUR SERVER FOR A FRESH DEFAULT CONFIGURATION.


// Enables the mod
// Options: true, false
"MOD_ENABLED": true,

// Forces players to die immediately if they are alone in the server instead of being downed
// Options: true, false
"SKIP_DOWNED_STATE_IF_NO_OTHER_PLAYERS_ONLINE": true,

// Requires players to use an empty hand to revive a downed player (can't hold a pickaxe or other item)
// Options: true, false
"REVIVING_REQUIRES_EMPTY_HAND": true,

// Makes players die immediately instead of being downed in lava 
// (it doesn't make sense to be swimming in lava)
// NOTE: the void instantly kills players no matter what
// Options: true, false
"ALLOW_DOWNED_STATE_IN_LAVA": false,

// Uses overlay messages (above the experience bar) instead of chat messages
// NOTE: chat messages are throttled appropriately for revive progress percentage
// Options: true, false
"USE_OVERLAY_MESSAGES": true,

// Give players that are downed a glow effect
// NOTE: not visible on Geyser Bedrock Clients
// Options: true, false
"DOWNED_PLAYERS_HAVE_GLOW_EFFECT": true,

// Give players that are downed a blindness effect
// Options: true, false
"DOWNED_PLAYERS_HAVE_BLINDNESS_EFFECT": true,

// Create a custom temporary team to change glow and name colors 
// NOTE: team will not be changed if player is already part of team, regardless of this setting for compatability
// Options: true, false
"USE_CUSTOM_DOWNED_TEAMS": true,

// How many ticks the revive penalty lasts for after being revived (20 per second)
// NOTE: after the cooldown the BLEEDING_OUT_DURATION_TICKS is reset
// Min: 0, Max: 999999999999999999
"REVIVE_PENALTY_COOLDOWN_TICKS": 600,

// Divides the remaining BLEEDING_OUT_DURATION_TICKS by this multiplier so that a 
// player bleeds out much faster after being revived over and over
// NOTE: a value of 4 would make it so that a player's remaining BLEEDING_OUT_DURATION_TICKS 
// are a quarter of what they were during the last time they were downed within the cooldown
// Min: 1 (disabled), Max: 999 (instant death after second time being downed before cooldown)
"REVIVE_PENALTY_MULTIPLIER": 4,

// How long in ticks a downed player has until they bleed out and die (20 per second)
// Min: -1 (Player never bleeds out and revive penalty does nothing), Max: 999999999999999999
"BLEEDING_OUT_DURATION_TICKS": 900,

// How long in ticks a player has to hold right click on a player to revive them
// Min: 0 (instant), Max: 999999999999999999 (virtually impossible)
"REVIVE_DURATION_TICKS": 60,

// How loud and how far away the heartbeat sound plays for nearby players
// Min: 0.0 (disabled) Max: 5.0
"HEARTBEAT_SOUND_VOLUME": 1.0,

// How loud and how far away the revived sound plays for nearby players
// Min: 0.0 (disabled) Max: 5.0
"REVIVED_SOUND_VOLUME": 2.5,

// How loud and how far away the downed sound plays for nearby players
// Min: 0.0 (disabled) Max: 5.0
"DOWNED_SOUND_VOLUME": 1.0,

// The base move speed to apply to the crawling downed player
// NOTE: after being revived, the player is set back to 0.1 base move speed, 
// a faster speed does not make sense when downed
// Min: 0.00001, Max: 0.1 (normal walking speed)
"DOWNED_MOVE_SPEED": 0.01
```

</details>


<details>
<summary>Known Bugs</summary>
  
Geyser Bedrock Clients:

- get slowly pushed around instead of crawling
- can't see glowing effect on downed players
  
Vanilla Clients:

- None (please report via github issues!)

</details>



Feel free to use this Mod in modpacks!

Disclaimer: This Mod is inspired by first persion shooters like Call of Duty, Fortnite, PUBG, Left For Dead, Borderlands, ect. that allow you to be down but not out instead of dying immediately but this mod is not affiliated with any franchise with this feature.

Report bugs and incompatible mods on github: [github.com/phasmoware/DownButNotOutMod/issues](https://github.com/phasmoware/DownButNotOutMod/issues)
