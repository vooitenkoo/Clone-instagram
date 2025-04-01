import React, {useState} from "react";
import Chat from "./component/Chat";  // Импортируем компонент чата

const ChatPage = () => {
    const [chatId, setChatId] = useState(1);  // Можно менять chatId динамически

    return (
        <div>
            <h1>Chat Page</h1>
            <button onClick={() => setChatId(1)}>Switch to Chat 2</button>  {/* Меняем чат */}
            <Chat chatId={chatId} />  {/* Передаем chatId */}
        </div>
    );
};

export default ChatPage;
