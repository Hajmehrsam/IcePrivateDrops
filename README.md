\# ❄️ IcePrivateDrops



> \*\*Private Item Protection for Minecraft Servers\*\*



\*\*IcePrivateDrops\*\* is a lightweight and reliable Minecraft plugin that protects dropped items from being picked up by other players for a configurable amount of time.



Players can safely collect their own drops first, while other players must wait until the protection period expires.



\---



\## ✨ Features



\* 🔒 \*\*Private Item Protection\*\*



&#x20; \* Dropped items are protected from other players.

&#x20; \* The player who dropped the item can pick it up immediately.



\* ⏱️ \*\*Configurable Protection Time\*\*



&#x20; \* Set exactly how long an item should remain private.

&#x20; \* After the timer expires, the item becomes available to everyone.



\* 💀 \*\*Death Drop Protection\*\*



&#x20; \* Protect items dropped when a player dies.

&#x20; \* Useful for Survival, SMP, RPG and PvP servers.



\* ⛏️ \*\*Block Drop Protection\*\*



&#x20; \* Items dropped from mining and block breaking can be protected.



\* 🌍 \*\*World Support\*\*



&#x20; \* Enable or disable private drops for specific worlds.



\* 🚪 \*\*Quit Protection\*\*



&#x20; \* Configure whether a player's protected drops remain private after they leave the server.



\* 🛡️ \*\*Permission-Based Control\*\*



&#x20; \* Flexible permissions allow server administrators to control who receives protection and who can bypass it.



\* ⚡ \*\*Lightweight \& Performance Friendly\*\*



&#x20; \* Designed to keep server overhead low.

&#x20; \* No unnecessary database or external dependencies.



\* ⚙️ \*\*Highly Configurable\*\*



&#x20; \* Configure protection duration, worlds, messages and behavior through the plugin configuration.



\---



\## 🎯 How It Works



When a player drops an item, IcePrivateDrops marks the item as private.



```text

Player drops item

&#x20;      │

&#x20;      ▼

&#x20;Item becomes private

&#x20;      │

&#x20;      ├── Owner → Can pick it up immediately

&#x20;      │

&#x20;      └── Other players → Cannot pick it up

&#x20;                        │

&#x20;                        ▼

&#x20;                 Protection expires

&#x20;                        │

&#x20;                        ▼

&#x20;                 Item becomes public

```



This prevents players from accidentally stealing or picking up another player's items.



\---



\## 🧩 Use Cases



IcePrivateDrops is especially useful for:



\* 🏹 Survival Servers

\* ⚔️ PvP Servers

\* 🌎 SMP Servers

\* 🏰 RPG Servers

\* 🏗️ Factions

\* 💀 Hardcore Servers

\* ⛏️ Prison Servers

\* 🌐 Network Servers



\---



\## ⚙️ Configuration



IcePrivateDrops is designed around a simple and customizable configuration system.



Example:



```yaml

\# Protection duration in seconds

protection-time: 30



\# Worlds where private drops are enabled

enabled-worlds:

&#x20; - world

&#x20; - world\_nether

&#x20; - world\_the\_end



\# Protect drops caused by player death

death-drops: true



\# Protect drops from breaking blocks

block-drops: true



\# Keep protection when the owner leaves the server

protect-on-quit: true

```



> Configuration options may vary depending on the installed version.



\---



\## 🔐 Permissions



| Permission                  | Description                                      |

| --------------------------- | ------------------------------------------------ |

| `iceprivatedrops.use`       | Allows the player to use private drop protection |

| `iceprivatedrops.death`     | Allows protection for death drops                |

| `iceprivatedrops.break`     | Allows protection for block drops                |

| `iceprivatedrops.quit`      | Keeps protected drops after quitting             |

| `iceprivatedrops.allworlds` | Enables protection in all worlds                 |

| `iceprivatedrops.ignore`    | Allows bypassing private drop protection         |



\---



\## 📦 Installation



1\. Download the latest \*\*IcePrivateDrops\*\* `.jar`.

2\. Place it inside your server's:



```text

/plugins

```



3\. Restart your server.

4\. Configure the plugin inside:



```text

/plugins/IcePrivateDrops/

```



5\. Restart or reload the plugin after making configuration changes.



\---



\## 🖥️ Compatibility



IcePrivateDrops is designed for modern \*\*Spigot/Paper-based Minecraft servers\*\*.



> Check the SpigotMC resource page for the latest supported Minecraft versions.



\---



\## 🚀 Why IcePrivateDrops?



Vanilla Minecraft doesn't provide a simple way to temporarily protect dropped items from other players.



IcePrivateDrops solves this problem with a lightweight system:



\*\*Drop → Protect → Owner collects → Timer expires → Public\*\*



No complicated setup.

No unnecessary systems.

Just reliable item protection.



\---



\## 📊 Performance



IcePrivateDrops is built with performance in mind.



\* ⚡ Lightweight event handling

\* 🧠 No unnecessary background processing

\* 💾 No external database required

\* 📉 Minimal server overhead

\* 🔧 Simple configuration



\---



\## 🛠️ Commands



IcePrivateDrops is primarily configuration and permission based.



If command functionality is available in your installed version, use:



```text

/iceprivatedrops

```



For the latest command list, check the plugin's SpigotMC documentation.



\---



\## 📝 Example



Imagine \*\*Steve\*\* mines a Diamond Ore.



Without IcePrivateDrops:



```text

Steve breaks Diamond Ore

&#x20;       ↓

Diamond drops

&#x20;       ↓

Alex can immediately pick it up

```



With IcePrivateDrops:



```text

Steve breaks Diamond Ore

&#x20;       ↓

Diamond becomes private

&#x20;       ↓

Steve → Can pick it up

Alex  → Cannot pick it up

&#x20;       ↓

Protection timer expires

&#x20;       ↓

Alex → Can now pick it up

```



\---



\## 🔄 Version



\*\*Current Version:\*\* `1.1.1`



\---



\## 👨‍💻 Developer



\*\*Developed by Hajmehrsam\*\*



Part of the \*\*Ice Plugins\*\* ecosystem.



\---



\## 📥 Download



You can find IcePrivateDrops on SpigotMC:



\*\*❄️ IcePrivateDrops | Private Item Protection\*\*



\---



\## 📜 License



This project is distributed under the license specified by the developer.



Please do not redistribute, re-upload or modify the plugin without permission.



\---



<p align="center">



\*\*❄️ IcePrivateDrops\*\*



\*Players Can Safely Collect Their Own Drops First.\*



Made with ❤️ for Minecraft servers.



</p>



