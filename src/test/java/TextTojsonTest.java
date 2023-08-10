import java.io.IOException;

public class TextTojsonTest {

    public static void main(String[] args) throws IOException {
        String string=new String("§e--------- §fHelp: Index §e---------------------------\n" +
                "§7Use /help [n] to get page n of help.\n" +
                "§6Aliases: §fLists command aliases\n" +
                "§6Bukkit: §fAll commands for Bukkit\n" +
                "§6chatsync: §fAll commands for chatsync\n" +
                "§6DoKeepInventory: §fAll commands for DoKeepInventory\n" +
                "§6Essentials: §fAll commands for Essentials\n" +
                "§6LaggRemoverPlus: §fAll commands for LaggRemoverPlus\n" +
                "§6LiteAnnouncer: §fAll commands for LiteAnnouncer\n" +
                "§6Minecraft: §fAll commands for Minecraft\n" +
                "§6/achievement: §fGives the specified player an achievement or changes a statistic value. Use '*' to give all achievements.\n" +
                "§6/afk: §fMarks you as away-from-keyboard.\n" +
                "§6/antioch: §fA little surprise for operators.\n" +
                "§6/back: §fTeleports you to your location prior to tp/spawn/warp.\n" +
                "§6/backup: §fRuns the backup if configured.\n" +
                "§6/balance: §fStates the current balance of a player.\n" +
                "§6/balancetop: §fGets the top balance values.\n" +
                "§6/ban: §fBans a player.\n" +
                "§6/ban-ip: §fPrevents the specified IP address from using this server\n" +
                "§6/banip: §fBans an IP address.\n" +
                "§6/banlist: §fView all players banned from this server\n" +
                "§6/bigtree: §fSpawn a big tree where you are looking.\n" +
                "§6/book: §fAllows reopening and editing of sealed books.\n" +
                "§6/break: §fBreaks the block you are looking at.\n" +
                "§6/broadcast: §fBroadcasts a message to the entire server.\n" +
                "§6/bukkit:ban: §fPrevents the specified player from using this server\n" +
                "§6/bukkit:clear: §fClears the player's inventory. Can specify item and data filters too.\n" +
                "§6/bukkit:enchant: §fAdds enchantments to the item the player is currently holding. Specify 0 for the level to remove an enchantment. Specify force to ignore normal enchantment restrictions\n" +
                "§6/bukkit:gamemode: §fChanges the player to a specific game mode\n" +
                "§6/bukkit:give: §fGives the specified player a certain amount of items\n" +
                "§6/bukkit:help: §fShows the help menu\n" +
                "§6/bukkit:kick: §fRemoves the specified player from the server\n" +
                "§6/bukkit:kill: §fCommits suicide, only usable as a player\n" +
                "§6/bukkit:list: §fLists all online players\n" +
                "§6/bukkit:me: §fPerforms the specified action in chat\n" +
                "§6/bukkit:pardon: §fAllows the specified player to use this server\n" +
                "§6/bukkit:tell: §fSends a private message to the given player\n" +
                "§6/bukkit:time: §fChanges the time on each world\n" +
                "§6/bukkit:tp: §fTeleports the given player (or yourself) to another player or coordinates\n" +
                "§6/bukkit:weather: §fChanges the weather\n" +
                "§6/bukkit:xp: §fGives the specified player a certain amount of experience. Specify <amount>L to give levels instead, with a negative amount resulting in taking levels.\n" +
                "§6/burn: §fSet a player on fire.\n" +
                "§6/clearinventory: §fClear all items in your inventory.\n" +
                "§6/compass: §fDescribes your current bearing.\n" +
                "§6/condense: §fCondenses items into a more compact blocks.\n" +
                "§6/customtext: §fAllows you to create custom text commands.\n" +
                "§6/defaultgamemode: §fSet the default gamemode\n" +
                "§6/delhome: §fRemoves a home.\n" +
                "§6/deljail: §fRemoves a jail.\n" +
                "§6/delwarp: §fDeletes the specified warp.\n" +
                "§6/deop: §fTakes the specified player's operator status\n" +
                "§6/depth: §fStates current depth, relative to sea level.\n" +
                "§6/difficulty: §fSets the game difficulty\n" +
                "§6/dokeepinventory: §fUse to reload plugin\n" +
                "§6/eco: §fManages the server economy.\n" +
                "§6/effect: §fAdds/Removes effects on players\n" +
                "§6/enchant: §fEnchants the item the user is holding.\n" +
                "§6/enderchest: §fLets you see inside an enderchest.\n" +
                "§6/essentials: §fReloads essentials.\n" +
                "§6/exp: §fGive, set or look at a players exp.\n" +
                "§6/ext: §fExtinguish players.\n" +
                "§6/feed: §fSatisfy the hunger.\n" +
                "§6/fireball: §fThrow a fireball.\n" +
                "§6/firework: §fAllows you to modify a stack of fireworks.\n" +
                "§6/fly: §fTake off, and soar!\n" +
                "§6/gamemode: §fChange player gamemode.\n" +
                "§6/gamerule: §fSets a server's game rules\n" +
                "§6/gc: §fReports memory, uptime and tick info.\n" +
                "§6/getpos: §fGet your current coordinates or those of a player.\n" +
                "§6/give: §fGive a player an item.\n" +
                "§6/god: §fEnables your godly powers.\n" +
                "§6/hat: §fGet some cool new headgear.\n" +
                "§6/heal: §fHeals you or the given player.\n" +
                "§6/help: §fViews a list of available commands.\n" +
                "§6/helpop: §fMessage online admins.\n" +
                "§6/home: §fTeleport to your home.\n" +
                "§6/ignore: §fIgnore or unignore other players.\n" +
                "§6/info: §fShows information set by the server owner.\n" +
                "§6/invsee: §fSee the inventory of other players.\n" +
                "§6/item: §fSpawn an item.\n" +
                "§6/itemdb: §fSearches for an item.\n" +
                "§6/jails: §fList all jails.\n" +
                "§6/jump: §fJumps to the nearest block in the line of sight.\n" +
                "§6/kick: §fKicks a specified player with a reason.\n" +
                "§6/kickall: §fKicks all players off the server except the issuer.\n" +
                "§6/kill: §fKills specified player.\n" +
                "§6/kit: §fObtains the specified kit or views all available kits.\n" +
                "§6/kittycannon: §fThrow an exploding kitten at your opponent.\n" +
                "§6/la: §fLiteAnnouncer main command.\n" +
                "§6/laggremoverplus: §f\n" +
                "§6/lightning: §fThe power of Thor. Strike at cursor or player.\n" +
                "§6/list: §fList all online players.\n" +
                "§6/mail: §fManages inter-player, intra-server mail.\n" +
                "§6/me: §fDescribes an action in the context of the player.\n" +
                "§6/minecraft:achievement: §fA Mojang provided command.\n" +
                "§6/minecraft:ban: §fA Mojang provided command.\n" +
                "§6/minecraft:ban-ip: §fA Mojang provided command.\n" +
                "§6/minecraft:banlist: §fA Mojang provided command.\n" +
                "§6/minecraft:clear: §fA Mojang provided command.\n" +
                "§6/minecraft:defaultgamemode: §fA Mojang provided command.\n" +
                "§6/minecraft:deop: §fA Mojang provided command.\n" +
                "§6/minecraft:difficulty: §fA Mojang provided command.\n" +
                "§6/minecraft:effect: §fA Mojang provided command.\n" +
                "§6/minecraft:enchant: §fA Mojang provided command.\n" +
                "§6/minecraft:gamemode: §fA Mojang provided command.\n" +
                "§6/minecraft:gamerule: §fA Mojang provided command.\n" +
                "§6/minecraft:give: §fA Mojang provided command.\n" +
                "§6/minecraft:help: §fA Mojang provided command.\n" +
                "§6/minecraft:kick: §fA Mojang provided command.\n" +
                "§6/minecraft:kill: §fA Mojang provided command.\n" +
                "§6/minecraft:list: §fA Mojang provided command.\n" +
                "§6/minecraft:me: §fA Mojang provided command.\n" +
                "§6/minecraft:op: §fA Mojang provided command.\n" +
                "§6/minecraft:pardon: §fA Mojang provided command.\n" +
                "§6/minecraft:pardon-ip: §fA Mojang provided command.\n" +
                "§6/minecraft:playsound: §fA Mojang provided command.\n" +
                "§6/minecraft:say: §fA Mojang provided command.\n" +
                "§6/minecraft:scoreboard: §fA Mojang provided command.\n" +
                "§6/minecraft:seed: §fA Mojang provided command.\n" +
                "§6/minecraft:setidletimeout: §fA Mojang provided command.\n" +
                "§6/minecraft:setworldspawn: §fA Mojang provided command.\n" +
                "§6/minecraft:spawnpoint: §fA Mojang provided command.\n" +
                "§6/minecraft:spreadplayers: §fA Mojang provided command.\n" +
                "§6/minecraft:tell: §fA Mojang provided command.\n" +
                "§6/minecraft:testfor: §fA Mojang provided command.\n" +
                "§6/minecraft:time: §fA Mojang provided command.\n" +
                "§6/minecraft:toggledownfall: §fA Mojang provided command.\n" +
                "§6/minecraft:tp: §fA Mojang provided command.\n" +
                "§6/minecraft:weather: §fA Mojang provided command.\n" +
                "§6/minecraft:whitelist: §fA Mojang provided command.\n" +
                "§6/minecraft:xp: §fA Mojang provided command.\n" +
                "§6/more: §fFills the item stack in hand to maximum size.\n" +
                "§6/motd: §fViews the Message Of The Day.\n" +
                "§6/msg: §fSends a private message to the specified player.\n" +
                "§6/mute: §fMutes or unmutes a player.\n" +
                "§6/near: §fLists the players near by or around a player.\n" +
                "§6/netstat: §fA Mojang provided command.\n" +
                "§6/nick: §fChange your nickname or that of another player.\n" +
                "§6/nuke: §fMay death rain upon them.\n" +
                "§6/op: §fGives the specified player operator status\n" +
                "§6/pardon-ip: §fAllows the specified IP address to use this server\n" +
                "§6/pay: §fPays another player from your balance.\n" +
                "§6/ping: §fPong!\n" +
                "§6/playsound: §fPlays a sound to a given player\n" +
                "§6/plugins: §fGets a list of plugins running on the server\n" +
                "§6/potion: §fAdds custom potion effects to a potion.\n" +
                "§6/powertool: §fAssigns a command to the item in hand.\n" +
                "§6/powertooltoggle: §fEnables or disables all current powertools.\n" +
                "§6/ptime: §fAdjust player's client time. Add @ prefix to fix.\n" +
                "§6/pweather: §fAdjust a player's weather\n" +
                "§6/qqmsg: §fQQ MSG sync\n" +
                "§6/r: §fQuickly reply to the last player to message you.\n" +
                "§6/realname: §fDisplays the username of a user based on nick.\n" +
                "§6/recipe: §fDisplays how to craft items.\n" +
                "§6/reload: §fReloads the server configuration and plugins\n" +
                "§6/remove: §fRemoves entities in your world.\n" +
                "§6/repair: §fRepairs the durability of one or all items.\n" +
                "§6/restart: §fRestarts the server\n" +
                "§6/rules: §fViews the server rules.\n" +
                "§6/save-all: §fSaves the server to disk\n" +
                "§6/save-off: §fDisables server autosaving\n" +
                "§6/save-on: §fEnables server autosaving\n" +
                "§6/say: §fBroadcasts the given message as the sender\n" +
                "§6/scoreboard: §fScoreboard control\n" +
                "§6/seed: §fShows the world seed\n" +
                "§6/seen: §fShows the last logout time of a player.\n" +
                "§6/sell: §fSells the item currently in your hand.\n" +
                "§6/setblock: §fA Mojang provided command.\n" +
                "§6/sethome: §fSet your home to your current location.\n" +
                "§6/setidletimeout: §fSets the server's idle timeout\n" +
                "§6/setjail: §fCreates a jail where you specified named [jailname].\n" +
                "§6/setwarp: §fCreates a new warp.\n" +
                "§6/setworldspawn: §fSets a worlds's spawn point. If no coordinates are specified, the player's coordinates will be used.\n" +
                "§6/setworth: §fSet the sell value of an item.\n" +
                "§6/skull: §fSet the owner of a player skull\n" +
                "§6/socialspy: §fToggles if you can see msg/mail commands in chat.\n" +
                "§6/spawner: §fChange the mob type of a spawner.\n" +
                "§6/spawnmob: §fSpawns a mob.\n" +
                "§6/spawnpoint: §fSets a player's spawn point\n" +
                "§6/speed: §fChange your speed limits.\n" +
                "§6/spreadplayers: §fSpreads players around a point\n" +
                "§6/stop: §fStops the server with optional reason\n" +
                "§6/sudo: §fMake another user perform a command.\n" +
                "§6/suicide: §fCauses you to perish.\n" +
                "§6/summon: §fA Mojang provided command.\n" +
                "§6/tellraw: §fA Mojang provided command.\n" +
                "§6/tempban: §fTemporary ban a user.\n" +
                "§6/testfor: §fTests whether a specifed player is online\n" +
                "§6/testforblock: §fA Mojang provided command.\n" +
                "§6/thunder: §fEnable/disable thunder.\n" +
                "§6/time: §fDisplay/Change the world time. Defaults to current world.\n" +
                "§6/timings: §fManages Spigot Timings data to see performance of the server.\n" +
                "§6/toggledownfall: §fToggles rain on/off on a given world\n" +
                "§6/togglejail: §fJails/Unjails a player, TPs them to the jail specified.\n" +
                "§6/top: §fTeleport to the highest block at your current position.\n" +
                "§6/tp: §fTeleport to a player.\n" +
                "§6/tpa: §fRequest to teleport to the specified player.\n" +
                "§6/tpaall: §fRequests all players online to teleport to you.\n" +
                "§6/tpaccept: §fAccepts a teleport request.\n" +
                "§6/tpahere: §fRequest that the specified player teleport to you.\n" +
                "§6/tpall: §fTeleport all online players to another player.\n" +
                "§6/tpdeny: §fReject a teleport request.\n" +
                "§6/tphere: §fTeleport a player to you.\n" +
                "§6/tpo: §fTeleport override for tptoggle.\n" +
                "§6/tpohere: §fTeleport here override for tptoggle.\n" +
                "§6/tppos: §fTeleport to coordinates.\n" +
                "§6/tps: §fGets the current ticks per second for the server\n" +
                "§6/tptoggle: §fBlocks all forms of teleportation.\n" +
                "§6/tree: §fSpawn a tree where you are looking.\n" +
                "§6/unban: §fUnbans the specified player.\n" +
                "§6/unbanip: §fUnbans the specified IP address.\n" +
                "§6/unlimited: §fAllows the unlimited placing of items.\n" +
                "§6/vanish: §fHide yourself from other players.\n" +
                "§6/version: §fGets the version of this server including any plugins in use\n" +
                "§6/warp: §fList all warps or warp to the specified location.\n" +
                "§6/weather: §fSets the weather.\n" +
                "§6/whitelist: §fManages the list of players allowed to use this server\n" +
                "§6/whois: §fDetermine the username behind a nickname.\n" +
                "§6/workbench: §fOpens up a workbench.\n" +
                "§6/world: §fSwitch between worlds.\n" +
                "§6/worth: §fCalculates the worth of items in hand or as specified.\n");
        long start = System.currentTimeMillis();
        //File file = toImg(string);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println(timeElapsed);
    }
}
