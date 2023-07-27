<!DOCTYPE html>
<html>
<head>
  <title>Add members</title>
  <style>
    body {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      margin: 0;
    }

    .form-container {
      text-align: center;
    }

    .form-field {
      margin: 10px;
    }

    label {
      display: block;
      font-weight: bold;
    }

    input[type="text"],
    input[type="password"],
    input[type="number"] {
      width: 200px;
      padding: 5px;
    }

    input[type="submit"] {
      background-color: #4CAF50;
      color: white;
      padding: 10px 20px;
      border: none;
      cursor: pointer;
    }

    input[type="submit"]:hover {
      background-color: #45a049;
    }
  </style>
</head>
<body>
        <form method="POST" action="/admin/addmembers">
        @csrf
        <div class="form-field">
            <h1>Hello,ready to add some new member?!!!</h1>
        </div>
        <div class="form-container">
            <div class="form-field">
            <label for="member_number">Enter Member Number:</label>
            <input type="text" id="member_number" name="member_number" placeholder="M001" required>
            </div>

            <div class="form-field">
            <label for="username">Input Username:</label>
            <input type="text" id="username" name="username" placeholder="Elsa"required>
            </div>

            <div class="form-field">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
            </div>

            <div class="form-field">
            <label for="phone_number">Phone Number:</label>
            <input type="number" id="phone_number" name="phone_number" placeholder="0757291160"required>
            </div>

            <div class="form-field">
            <input type="submit" value="Add Member">
            </div>
        </div>
    </form>
</div>
</body>
</html>
