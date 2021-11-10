import { bootstrap } from './backend/src/main-nest';

export async function launchCapture() {
    try {
       await bootstrap();
    } catch (err) {
        console.error("Error in process : " + err.stack);
    }
}
