<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Member extends Model
{
    protected $fillable = ['member_number', 'username', 'password', 'phone_number', 'active_status'];
    use HasFactory;
}
