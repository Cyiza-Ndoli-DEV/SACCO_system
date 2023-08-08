<?php

namespace App\Http\Controllers;

use App\Models\Reference;
use App\Models\RecommendedList;

class ReferenceController extends Controller
{
    public function displayData()
    {
        // Assuming you have a "Reference" model and you want to fetch data from it
        $references = Reference::whereNull('response')
                       ->orWhere('response', '')
                       ->get();

        $list = RecommendedList::where ('status', 'pending')->get();
        return view('pages.table', compact('references','list'));
    }
    
}


