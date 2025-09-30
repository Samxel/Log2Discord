# Log2Discord

This mod mirrors your Minecraft **server console logs** to a Discord channel using a configurable Webhook.  
It provides clean message formatting and simple admin commands for configuration.

<p align="center">
  <img src="https://github.com/user-attachments/assets/11b2c473-d150-43bd-b3ea-7ee492b6eb6a" 
       alt="log2discord"
       width="200" />


</p>
<p align="center">
  <a href="https://modrinth.com/mod/log2discord">
    <img src="https://img.shields.io/badge/Modrinth-Download-brightgreen?logo=modrinth&style=for-the-badge" alt="Modrinth">
  </a>
</p>

## Features
- Sends server console output (INFO, WARN, ERROR …) directly to a Discord channel  
- Easy configuration via JSON file or in-game commands  

## Configuration
`config/log2discord.json`:

```json
{
  "webhookUrl": "https://discord.com/api/webhooks/…"
}
```

## Commands
Available only to server operators:

- `/log2discord reload`  
  Reloads the configuration from the JSON file  

- `/log2discord setWebhook <url>`  
  Updates the webhook and saves it into the config  

## Example Output
<img width="1283" height="701" alt="Discord_Yn9WnFgfVb" src="https://github.com/user-attachments/assets/ce4b67cb-6dc9-4933-8550-6968d3089933" />


## Notes
- This mod is **server-side only** and does not need to be installed by clients.  
- Works with Fabric 1.21.9.  
- Make sure to keep your webhook URL private — anyone with this URL can post into your Discord channel.
