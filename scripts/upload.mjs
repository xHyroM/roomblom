import { fetch } from 'undici';
import FormData from 'form-data';
import { readFile } from 'fs/promises';

const file = await readFile('build/libs/roomblom-1.0.0-all.jar');
const form = new FormData();
form.append('files', file, 'roomblom.jar');

const token = await fetch(`https://panel.jopgamer.xyz/api/client/servers/1e910635/files/upload`, {
    method: 'GET',
    headers: {
        Authorization: `Bearer ${process.env.PANEL_API_TOKEN}`
    }
}).catch(e => console.log(e));

console.log(token.status);

const response = await fetch(`${(await token.json()).attributes.url}&directory=/`, {
    method: 'POST',
    headers: {
        ...form.getHeaders(),
        Authorization: `Bearer ${process.env.PANEL_API_TOKEN}`
    },
    body: form.getBuffer()
}).catch(e => console.log(e));

console.log(response.status);