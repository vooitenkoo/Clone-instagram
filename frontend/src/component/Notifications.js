// import React, { useEffect } from "react";
//
// const Notifications = ({ notifications }) => {
//     useEffect(() => {
//         // Загружаем существующие уведомления при монтировании
//         const token = localStorage.getItem("authToken");
//         fetch("http://localhost:8082/notifications", {
//             headers: {
//                 Authorization: `Bearer ${token}`,
//             },
//         })
//             .then((response) => response.json())
//             .then((data) => {
//                 // Устанавливаем начальные уведомления (если нужно)
//                 // setNotifications(data); // Раскомментировать, если хочешь заменить, а не дополнять
//             })
//             .catch((error) => console.error("Error fetching notifications:", error));
//     }, []);
//
//     return (
//         <div>
//             <h2>Notifications</h2>
//             <ul>
//                 {notifications.map((notif) => (
//                     <li key={notif.id}>
//                         {notif.message} - {notif.type}
//                         {notif.entityId && <a href={`/chat/${notif.entityId}`}> Go to chat</a>}
//                     </li>
//                 ))}
//             </ul>
//         </div>
//     );
// };
//
// export default Notifications;