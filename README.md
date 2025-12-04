# A Fully Server-Side Player Downed and Revive Mod

![Down But Not Out Logo in minecraft-like font](https://cdn.modrinth.com/data/cached_images/84fd8f68e4167ecba7b2c46e6f13f53138dc35f9.png)

Revive downed players: instead of dying, players drop to half a heart and crawl. Inspired by Call of Duty Zombies. Fully server-side and compatible with vanilla and Geyser Bedrock clients.

To revive your downed friend, simply hold **right-click** with an empty hand on them (or **left trigger** on a Geyser Bedrock Client)

## Features

#### Getting Downed and Revived

- Players are downed instead of dying, dropped to half a heart and forced to crawl.
- Other players can revive downed players by holding right-click; progress is shown with a percentage and light blue color outline.
- Downed players bleed out over time (45 seconds by default) and die if not revived.
- Player names and outline colors shift from yellow to dark red based on remaining time before bleeding out.
- A /bleedout clickable command lets players give up early and respawn.

#### Compatibility

- Fully server-side; **no client install required** for Java Edition.
- Works with Geyser Bedrock clients (minor visual limitations only).
- Designed for broad compatibility with grave mods.
- Perfect for modpacks.


#### Gameplay Integrity

- Preserves the original damage source for accurate death messages.
- Penalizes players who log out while downed or are downed repeatedly by shortening the bleed-out timer.
- Does not interfere with **respawn anchors**, **death statistics**, or **totems of undying**.

#### Audio Feedback

- Includes vanilla sounds for being revived, being downed, and a progressive heartbeat during bleed-out.

#### Lightweight Dependencies

- Only requires Fabric API
- Currently under 50KB jar size

#### Robust configuration for Server Administrators

*   See below:

<details>
<summary>Configuration</summary>
[config location]/DownButNotOut/config.json

```json5
{
  // WARNING: DO NOT COPY AND PASTE THESE COMMENTS, INVALID JSON WILL HALT THE STARTUP.
  // SIMPLY DELETE THE CONFIG AND RESTART YOUR SERVER FOR A FRESH DEFAULT CONFIGURATION.


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
  // Makes a name tag with "◥REVIVE◤" hover above a downed player visible
  // Options: true, false
  "SHOW_REVIVE_TAG_ABOVE_PLAYER": true,
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
  // How long in ticks a player has to hold right-click on a player to revive them
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
}
```
</details>

<details>
<summary>Known Bugs</summary>

Geyser Bedrock Clients:

- slowly walk instead of crawling
- can't see glowing effect on downed players

Vanilla Clients:
- None (please report via GitHub issues!)

</details>

## How to Use

*   Place the **.jar** file directly into the Fabric server’s mods folder. **Do not** install it on the client side.
*   Make sure **Fabric API** is also in the server's mods folder.
*   Have your friends join the server normal and enjoy! No extra steps for Geyser clients, it just works!


### Disclaimer

This Mod is inspired by first-person shooters like Call of Duty, Fortnite, PUBG, Left For Dead, Borderlands, ect. that allow you to be downed instead of dying and revived but this mod is not affiliated with any franchise with this gameplay feature.

### License

© phasmoware

Check the source code for the MIT License

**Feel free to use this Mod in modpacks!**