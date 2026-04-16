import { useEffect, useState } from "react";

const apiBase = "http://localhost:8081/api";

const emptyMenuForm = {
  itemName: "",
  price: "",
  category: "FOOD",
  available: true,
  description: ""
};

async function api(path, options = {}) {
  const response = await fetch(`${apiBase}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: "Request failed" }));
    throw new Error(error.message || "Request failed");
  }

  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

function normalizeCategory(item) {
  return String(item?.category || "FOOD").trim().toUpperCase();
}

export default function App() {
  const [mode, setMode] = useState("login");
  const [loginForm, setLoginForm] = useState({ email: "", password: "" });
  const [registerForm, setRegisterForm] = useState({ name: "", email: "", password: "" });
  const [user, setUser] = useState(null);
  const [menu, setMenu] = useState([]);
  const [cart, setCart] = useState([]);
  const [orders, setOrders] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [users, setUsers] = useState([]);
  const [adminOrders, setAdminOrders] = useState([]);
  const [adminMenu, setAdminMenu] = useState([]);
  const [salesDate, setSalesDate] = useState(new Date().toISOString().slice(0, 10));
  const [sales, setSales] = useState(null);
  const [walletAmount, setWalletAmount] = useState("");
  const [menuForm, setMenuForm] = useState(emptyMenuForm);
  const [editingMenuId, setEditingMenuId] = useState(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    api("/menu")
      .then(setMenu)
      .catch((error) => setMessage(error.message));
  }, []);

  async function loadUserData(currentUser) {
    const [freshUser, userOrders, walletTransactions] = await Promise.all([
      api(`/users/${currentUser.id}`),
      api(`/users/${currentUser.id}/orders`),
      api(`/users/${currentUser.id}/wallet/transactions`)
    ]);

    setUser(freshUser);
    setOrders(userOrders);
    setTransactions(walletTransactions);
    setMenu(await api("/menu"));

    if (freshUser.role === "ADMIN") {
      await loadAdminData();
    }
  }

  async function loadAdminData() {
    const [allUsers, allOrders, fullMenu, dailySales] = await Promise.all([
      api("/admin/users"),
      api("/admin/orders"),
      api("/admin/menu"),
      api(`/admin/reports/daily-sales?date=${salesDate}`)
    ]);

    setUsers(allUsers);
    setAdminOrders(allOrders);
    setAdminMenu(fullMenu);
    setSales(dailySales);
  }

  async function handleLogin(event) {
    event.preventDefault();
    setLoading(true);
    try {
      const loggedInUser = await api("/auth/login", {
        method: "POST",
        body: JSON.stringify(loginForm)
      });
      setMessage(`Logged in as ${loggedInUser.name}`);
      await loadUserData(loggedInUser);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleRegister(event) {
    event.preventDefault();
    setLoading(true);
    try {
      const registeredUser = await api("/auth/register", {
        method: "POST",
        body: JSON.stringify(registerForm)
      });
      setMode("login");
      setMessage(`Account created for ${registeredUser.email}. You can log in now.`);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  function addToCart(item) {
    setCart((current) => {
      const existing = current.find((entry) => entry.id === item.id);
      if (existing) {
        return current.map((entry) =>
          entry.id === item.id ? { ...entry, quantity: entry.quantity + 1 } : entry
        );
      }
      return [...current, { ...item, quantity: 1 }];
    });
  }

  function updateCart(itemId, quantity) {
    setCart((current) =>
      current
        .map((entry) => (entry.id === itemId ? { ...entry, quantity: Number(quantity) } : entry))
        .filter((entry) => entry.quantity > 0)
    );
  }

  async function placeOrder() {
    if (!user || cart.length === 0) {
      return;
    }

    setLoading(true);
    try {
      await api(`/users/${user.id}/orders`, {
        method: "POST",
        body: JSON.stringify({
          items: cart.map((item) => ({ itemId: item.id, quantity: item.quantity }))
        })
      });
      setCart([]);
      await loadUserData(user);
      setMessage("Order placed successfully.");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function topUpWallet(event) {
    event.preventDefault();
    if (!user) {
      return;
    }

    setLoading(true);
    try {
      await api(`/users/${user.id}/wallet/top-up`, {
        method: "POST",
        body: JSON.stringify({ amount: Number(walletAmount) })
      });
      setWalletAmount("");
      await loadUserData(user);
      setMessage("Wallet updated.");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function submitMenuItem(event) {
    event.preventDefault();
    setLoading(true);
    const path = editingMenuId ? `/admin/menu/${editingMenuId}` : "/admin/menu";
    const method = editingMenuId ? "PUT" : "POST";

    try {
      await api(path, {
        method,
        body: JSON.stringify({ ...menuForm, price: Number(menuForm.price) })
      });
      setMenuForm(emptyMenuForm);
      setEditingMenuId(null);
      await loadAdminData();
      setMessage("Menu saved.");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function removeMenuItem(itemId) {
    setLoading(true);
    try {
      await api(`/admin/menu/${itemId}`, { method: "DELETE" });
      await loadAdminData();
      setMessage("Menu item deleted.");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function refreshSales() {
    try {
      setSales(await api(`/admin/reports/daily-sales?date=${salesDate}`));
    } catch (error) {
      setMessage(error.message);
    }
  }

  const cartTotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const isAdmin = user?.role === "ADMIN";
  const foodItems = menu.filter((item) => normalizeCategory(item) !== "BEVERAGE");
  const beverageItems = menu.filter((item) => normalizeCategory(item) === "BEVERAGE");

  return (
    <div className="page-shell">
      <div className="hero-panel">
        <div>
          <p className="eyebrow">DBMS Project Starter</p>
          <h1>CanteenManagement</h1>
          <p className="hero-copy">
            Cashless food ordering for colleges and organizations with proper user, order,
            menu, and transaction management.
          </p>
        </div>
      </div>

      {message ? <div className="flash">{message}</div> : null}

      {!user ? (
        <section className="auth-grid">
          <form className="card" onSubmit={handleLogin}>
            <div className="section-head">
              <h2>Login</h2>
              <button type="button" className="ghost" onClick={() => setMode(mode === "login" ? "register" : "login")}>
                {mode === "login" ? "Need an account?" : "Already registered?"}
              </button>
            </div>
            <label>Email</label>
            <input
              value={loginForm.email}
              onChange={(event) => setLoginForm({ ...loginForm, email: event.target.value })}
              placeholder="student@canteen.com"
            />
            <label>Password</label>
            <input
              type="password"
              value={loginForm.password}
              onChange={(event) => setLoginForm({ ...loginForm, password: event.target.value })}
              placeholder="password123"
            />
            <button disabled={loading}>{loading ? "Please wait..." : "Login"}</button>
          </form>

          <form className={`card ${mode === "register" ? "" : "muted-card"}`} onSubmit={handleRegister}>
            <div className="section-head">
              <h2>Register</h2>
              <span className="mini-note">Create student account</span>
            </div>
            <label>Name</label>
            <input
              value={registerForm.name}
              onChange={(event) => setRegisterForm({ ...registerForm, name: event.target.value })}
              placeholder="Dhyan Patel"
            />
            <label>Email</label>
            <input
              value={registerForm.email}
              onChange={(event) => setRegisterForm({ ...registerForm, email: event.target.value })}
              placeholder="student@ahduni.edu.in"
            />
            <label>Password</label>
            <input
              type="password"
              value={registerForm.password}
              onChange={(event) => setRegisterForm({ ...registerForm, password: event.target.value })}
              placeholder="Create a password"
            />
            <button disabled={loading}>{loading ? "Please wait..." : "Register"}</button>
          </form>
        </section>
      ) : (
        <section className="dashboard">
          <div className="topbar">
            <div>
              <p className="eyebrow">Welcome</p>
              <h2>{user.name}</h2>
              <p className="mini-note">{user.email} • {user.role}</p>
            </div>
            <button
              className="ghost danger"
              onClick={() => {
                setUser(null);
                setCart([]);
                setMessage("Logged out.");
              }}
            >
              Logout
            </button>
          </div>

          {!isAdmin ? (
            <>
              <div className="content-grid">
                <div className="card">
                  <div className="section-head">
                    <h3>Wallet</h3>
                    <strong>Rs. {Number(user.walletBalance).toFixed(2)}</strong>
                  </div>
                  <form className="inline-form" onSubmit={topUpWallet}>
                    <input
                      type="number"
                      min="1"
                      step="1"
                      value={walletAmount}
                      onChange={(event) => setWalletAmount(event.target.value)}
                      placeholder="Add amount"
                    />
                    <button disabled={loading}>Top Up</button>
                  </form>
                </div>

                <div className="card">
                  <div className="section-head">
                    <h3>Cart</h3>
                    <strong>Rs. {cartTotal.toFixed(2)}</strong>
                  </div>
                  {cart.length === 0 ? <p className="mini-note">No items selected yet.</p> : null}
                  <div className="stack-list">
                    {cart.map((item) => (
                      <div className="list-row" key={item.id}>
                        <div>
                          <strong>{item.itemName}</strong>
                          <p>Rs. {Number(item.price).toFixed(2)}</p>
                        </div>
                        <input
                          type="number"
                          min="0"
                          value={item.quantity}
                          onChange={(event) => updateCart(item.id, event.target.value)}
                        />
                      </div>
                    ))}
                  </div>
                  <button disabled={loading || cart.length === 0} onClick={placeOrder}>
                    Place Order
                  </button>
                </div>
              </div>

              <div className="section-head standalone">
                <h3>Food Menu</h3>
              </div>
              <div className="menu-grid">
                {foodItems.map((item) => (
                  <article className="menu-card" key={item.id}>
                    <div>
                      <h4>{item.itemName}</h4>
                      <p>{item.description || "Freshly prepared item."}</p>
                    </div>
                    <div className="menu-card-footer">
                      <strong>Rs. {Number(item.price).toFixed(2)}</strong>
                      <button onClick={() => addToCart(item)}>Add</button>
                    </div>
                  </article>
                ))}
              </div>

              <div className="section-head standalone">
                <h3>Beverages</h3>
              </div>
              <div className="menu-grid">
                {beverageItems.map((item) => (
                  <article className="menu-card" key={item.id}>
                    <div>
                      <h4>{item.itemName}</h4>
                      <p>{item.description || "Freshly prepared item."}</p>
                    </div>
                    <div className="menu-card-footer">
                      <strong>Rs. {Number(item.price).toFixed(2)}</strong>
                      <button onClick={() => addToCart(item)}>Add</button>
                    </div>
                  </article>
                ))}
              </div>

              <div className="content-grid lower">
                <div className="card">
                  <div className="section-head">
                    <h3>Recent Orders</h3>
                  </div>
                  <div className="stack-list">
                    {orders.map((order) => (
                      <div className="order-box" key={order.id}>
                        <div className="section-head">
                          <strong>Order #{order.id}</strong>
                          <span>{order.status}</span>
                        </div>
                        <p>{new Date(order.orderDate).toLocaleString()}</p>
                        <p>Rs. {Number(order.totalAmount).toFixed(2)}</p>
                        <p>{order.items.map((item) => `${item.itemName} x${item.quantity}`).join(", ")}</p>
                      </div>
                    ))}
                    {orders.length === 0 ? <p className="mini-note">No orders yet.</p> : null}
                  </div>
                </div>

                <div className="card">
                  <div className="section-head">
                    <h3>Transaction History</h3>
                  </div>
                  <div className="stack-list">
                    {transactions.map((entry) => (
                      <div className="list-row" key={entry.id}>
                        <div>
                          <strong>{entry.transactionType}</strong>
                          <p>{entry.description}</p>
                        </div>
                        <div className="align-right">
                          <strong>Rs. {Number(entry.amount).toFixed(2)}</strong>
                          <p>{new Date(entry.transactionDate).toLocaleString()}</p>
                        </div>
                      </div>
                    ))}
                    {transactions.length === 0 ? <p className="mini-note">No wallet transactions yet.</p> : null}
                  </div>
                </div>
              </div>
            </>
          ) : null}

          {isAdmin ? (
            <section className="admin-section">
              <div className="section-head standalone">
                <h3>Admin Workspace</h3>
                <button className="ghost" onClick={loadAdminData}>Refresh Admin Data</button>
              </div>

              <div className="content-grid lower">
                <form className="card" onSubmit={submitMenuItem}>
                  <div className="section-head">
                    <h3>{editingMenuId ? "Edit Menu Item" : "Add Menu Item"}</h3>
                    {editingMenuId ? (
                      <button type="button" className="ghost" onClick={() => {
                        setEditingMenuId(null);
                        setMenuForm(emptyMenuForm);
                      }}>
                        Clear
                      </button>
                    ) : null}
                  </div>
                  <label>Item Name</label>
                  <input value={menuForm.itemName} onChange={(event) => setMenuForm({ ...menuForm, itemName: event.target.value })} />
                  <label>Price</label>
                  <input type="number" min="1" step="0.01" value={menuForm.price} onChange={(event) => setMenuForm({ ...menuForm, price: event.target.value })} />
                  <label>Category</label>
                  <select value={menuForm.category} onChange={(event) => setMenuForm({ ...menuForm, category: event.target.value })}>
                    <option value="FOOD">Food</option>
                    <option value="BEVERAGE">Beverage</option>
                  </select>
                  <label>Description</label>
                  <textarea value={menuForm.description} onChange={(event) => setMenuForm({ ...menuForm, description: event.target.value })} />
                  <label className="checkbox-row">
                    <input type="checkbox" checked={menuForm.available} onChange={(event) => setMenuForm({ ...menuForm, available: event.target.checked })} />
                    Available
                  </label>
                  <button disabled={loading}>{editingMenuId ? "Update Item" : "Create Item"}</button>
                </form>

                <div className="card">
                  <div className="section-head">
                    <h3>Daily Sales</h3>
                    <button className="ghost" onClick={refreshSales}>Check</button>
                  </div>
                  <input type="date" value={salesDate} onChange={(event) => setSalesDate(event.target.value)} />
                  {sales ? (
                    <div className="stats-grid">
                      <div><span>Total Sales</span><strong>Rs. {Number(sales.totalSales).toFixed(2)}</strong></div>
                      <div><span>Total Orders</span><strong>{sales.totalOrders}</strong></div>
                      <div><span>Transactions</span><strong>{sales.totalTransactions}</strong></div>
                    </div>
                  ) : (
                    <p className="mini-note">Select a date to view the report.</p>
                  )}
                </div>
              </div>

              <div className="content-grid lower">
                <div className="card">
                  <div className="section-head">
                    <h3>Users</h3>
                    <strong>{users.length}</strong>
                  </div>
                  <div className="stack-list">
                    {users.map((entry) => (
                      <div className="list-row" key={entry.id}>
                        <div>
                          <strong>{entry.name}</strong>
                          <p>{entry.email}</p>
                        </div>
                        <div className="align-right">
                          <strong>{entry.role}</strong>
                          <p>Rs. {Number(entry.walletBalance).toFixed(2)}</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="card">
                  <div className="section-head">
                    <h3>Manage Menu</h3>
                    <strong>{adminMenu.length}</strong>
                  </div>
                  <div className="stack-list">
                    {adminMenu.map((item) => (
                      <div className="list-row" key={item.id}>
                        <div>
                          <strong>{item.itemName}</strong>
                          <p>{item.category} • {item.available ? "Available" : "Unavailable"}</p>
                        </div>
                        <div className="action-row">
                          <button className="ghost" onClick={() => {
                            setEditingMenuId(item.id);
                            setMenuForm({
                              itemName: item.itemName,
                              price: item.price,
                              category: item.category,
                              available: item.available,
                              description: item.description || ""
                            });
                          }}>
                            Edit
                          </button>
                          <button className="ghost danger" onClick={() => removeMenuItem(item.id)}>
                            Delete
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div className="card">
                <div className="section-head">
                  <h3>All Orders</h3>
                  <strong>{adminOrders.length}</strong>
                </div>
                <div className="stack-list">
                  {adminOrders.map((order) => (
                    <div className="order-box" key={order.id}>
                      <div className="section-head">
                        <strong>Order #{order.id}</strong>
                        <span>{order.userName}</span>
                      </div>
                      <p>{new Date(order.orderDate).toLocaleString()}</p>
                      <p>Rs. {Number(order.totalAmount).toFixed(2)} • {order.status}</p>
                    </div>
                  ))}
                </div>
              </div>
            </section>
          ) : null}
        </section>
      )}
    </div>
  );
}
