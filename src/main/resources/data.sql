DELETE FROM users where username = 'admin6';
INSERT INTO users (id, username, password, role, email) VALUES
 (999999, 'admin6', '$2a$10$G.xHXY3QKap/oaBI4vjC7eDyeALo236Bjuk8RecY4hfFnSqg//SNK', 'ADMIN', 'admin6@admin');
