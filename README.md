Custom Skins Manager it's complete solution for the skin system for your server. The plugin allows you to install and manage your game skins as you wish. 
In addition to standard functions, such as changing the skin using a nickname, the plugin can install default skins instead of Steve and Alex, allows you to change the skin 
using a link to the image, which will allow players to show creativity if they do not have a permi account. Also, the plugin has many other functions and features, which will 
be described below.

#### Minimal tested version is 1.8.8

#### [API docs](https://gitlab.com/Nanit/custom-skins-manager/wikis/API_EN)
#### [SpigotMC page](https://www.spigotmc.org/resources/custom-skins-manager.57760/)
### Dependencies
1) ProtocolLib (important)  
2) Citizens (optional, for change npc skin via url)  

### Features
1) Change your skin via link to image
1.1) Change your skin via any premium nickname
2) Restore default premium skins on servers with `online-mode=false`
3) Change default skin (Steve, Alex) for players who not have premium skin
4) Useful skins menu
5) Blacklist and whitelist for nicknames or links
6) Opportunity to provide your own custom skin system if you have few premium accounts. (Not required MineSkin)
7) Config format is HOCON. This is very useful and simple format, better which 

> ! Notice  
> The plugin does not steal your license accounts. 
> If you use the skin system without the Meinskin service with your Modjang accounts,
> do not be afraid to use it. All source code of the plugin is published.

### How it work

When you execute command '/skin url' plugin get your image link.
Then your image used to upload and change skin for some minecraft premium account.
When skin successfully uploaded plugin try to take skin data via mojang session server.
Finaly taken skin data applied to your player profile and saved in database
In default plugin use MineSkin service for this, but if you have
few free premium accounts, you can use it and no independ from public services like MineSkin.

### Installation
##### Spigot
1) Copy jar file to plugin folder
2) Run server and wait while plugin creating configuration files
3) It's all. If you want change some data, you can change it in config.conf
##### BungeeCord
1) Copy jar file to spigot and bungeecord plugin folders
2) Run Spigot and BungeCord
3) In BungeeCord pluigin config change data for connecting to MySQL database.
4) In Spigot plugin config change parameter `bungeecord` to `true`
5) Restart BungeeCord and Spigot
6) Wow, its work!

If you use plugin with BungeeCord, all data need to change in Bungee plugin config. But GUI is tuned to Spigot.

### Commands
Aliases:
 - csm
 - skin
 - skins

Aliases you can change in plugin.yml inside jar file

##### Player commands:
`/skin player <player name> - Set skin via premium nickname`  
`/skin url <link to png image> [slim] - Set skin via link to image. If exist [slim] skin will have a slim model`  
`/skin reset - Reset skin to default`  
##### Admin commands:
`/skin npc <link to png image> - Set skin for Citizens NPC`
`/skin to <player> from <nickname> - Remote set skin for <player> from premium account <nickname>`  
`/skin to <player> url <link to image> [slim] - Remote set skin for <player> from link to image`  
`/skin to <player> reset - Reset skin for <player>`

### Permissions
`csm.skin.player` - Allow to setup skin from license name  
`csm.skin.url` - Allow to setup skin from url to image  
`csm.skin.menu` - Allow to open menu with users skins  
`csm.skin.reset` - Allow to reset skin to default  
`csm.skin.npc` - Allow to set skin via url for Citizens NPC  
`csm.skin.to` - Allow to setup skins for any player

### Language support
In default plugin support two language: English and Russian. But you can add you own language file. 
For this:  
1) Copy default language file in folder `lang`, 
2) Rename it as you want (recommend language code like `EN`, `DE`)
3) Change all values as you want.
4) In plugin config change parameter `language` to your file name (without extension)
5) Profit!