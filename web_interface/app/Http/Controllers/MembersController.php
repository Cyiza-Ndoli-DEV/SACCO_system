<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Member;
use PhpParser\Node\Stmt\TryCatch;

class MembersController extends Controller
{
    //check if user is logged in
    public function __construct()
    {
        $this->middleware('auth');
    }
    public function index()
    {

    }

    public function add_members()
    {
        return view('add_members');
    }
    public function store_members(Request $request)
    {
        // Validate the form data (optional but recommended)
        $request->validate([
            'member_number' => 'required|string',
            'username' => 'required|string',
            'password' => 'required|string',
            'phone_number' => 'required|string',
        ]);

        // Assuming you have a Member model to represent the database table
        $member = new \App\Models\Member();
        $member->member_number = $request->input('member_number');
        $member->username = $request->input('username');
        $member->password =($request->input('password')); // Hash the password for security
        $member->phone_number = $request->input('phone_number');
        $member->save();

        // Redirect the user back with a success message
        return redirect()->back()->with('success', 'Member added successfully!');
    }
} 

