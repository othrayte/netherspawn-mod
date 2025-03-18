# NetherSpawn

NetherSpawn is a Minecraft mod that changes the default spawn location to be in the Nether, preferably at a ruined
portal. It is designed to be used to make "Nether Escape" style modpacks.

## How is spawning changed?

The mod attempts to find a valid spawn location at ruined portal structures in the Nether, specifically looking for the
closest portals to (0,0). Each portal is checked for destruction and nearby valid spawn locations. Valid spawn locations
are chosen randomly each time a player spawns. If a portal is destroyed or lacks valid spawn locations, the mod will
look for the next closest portal.

Each time a player respawns at a ruined portal, the portal takes damage. If the portal is damaged too much, it is
considered destroyed, and the mod will no longer attempt to spawn the player there. This helps break the player out of a
death loop.

If no valid spawn location is found at any ruined portal, the mod will look for valid spawn locations as close to (0,0)
as possible.

The mod checks spawn locations more carefully than vanilla Minecraft to ensure the player will not immediately die upon
spawning. This includes avoiding blocks that would damage or suffocate the player or cause immediate movement due to
collision bounds. The mod also allows spawning on blocks like half slabs or stairs, which are common around ruined
portals.

## Adding this mod to an existing world

You can add this mod to an existing world. Players who would normally spawn at the overworld spawn point will now spawn
in the Nether. This does not affect players with a bed or respawn anchor set. New players and those without a valid bed
or respawn anchor will spawn in the Nether.

## Recent and potential future features

- [x] New players spawn in the Nether
- [x] Players that die without a bed or respawn anchor spawn in the Nether
- [ ] Configuration options for spawn behavior - Link to issue TBA
- [ ] Checks for bad spawn locations - Link to issue TBA  
      E.g. lava lake islands, enclosed spaces, etc.
- [ ] Choice of type of spawn location at world start - Link to issue TBA  
      E.g. Easy: portal in crimson forest near blackstone, Maddening: lava lake island
- [ ] Support for modded nether biomes - Link to issue TBA

## License

This mod is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Authors

- Adrian Cowan (othrayte)

## Contributing

Contributions are welcome! For small changes please fork the repository and submit a pull request, for larger changes please open a GitHub issue before you start working on it so that we can track it.

## Support

If you encounter any issues or have any questions, please open an issue on the [GitHub repository](https://github.com/othrayte/netherspawn/issues).